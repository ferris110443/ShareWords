"use strict";


const TIMESTAMP_BITS = 24 // Max num of chars is 2 ** 24
                          // Max num of replicas is 2 ** 8


// An RGA is a replicated string.
// constructor function for the RGA
function RGA(id) {
  this.id = id
  this.left = { timestamp: 0, removed: false, chr: "" }
  // the left sentinel node , used as a starting point for the linked list structure
  // the left sentinel node is always present and never removed
  this.index = new Map([[this.left.timestamp, this.left]])
  this.timestamp = null
  this.subscribers = []
}

// turn the RGA into an JS array
RGA.toArray = function (rga) {
  let ary = [];
  let curr = rga.left;

  while (curr) {
    ary.push(curr)
    curr = curr.next
  }

  return ary
}



RGA.prototype = { // define the RGA prototype object
  constructor: RGA // RGA is a function, so it has a constructor property

  // Public interface
  , subscribe: function (callback) {
    this.subscribers.push(callback)
  } //add a callback to the subscribers array


  //Receives an operation and applies it to the RGA.
  , receive: function(op) {
    return this[op.type].call(this, op)
  }


  // Sends an operation to all subscribers and applies it locally.
  , downstream: function (op) {
    const node = this.receive(op)
    op.sender = this.id

    if (node) { this.subscribers.forEach(callback => { callback(op) }) }

    return node
  }

  // Private
  , requestHistory: function () {
    this.subscribers.forEach(callback => {
      callback({
        type: "historyRequest",
        // since: timestamp,
        // sender: this.id,
        // recipient: sender
      })
    })
  }

  , historyRequest: function() {
    this.subscribers.forEach(callback => {
      callback({ type: "batch", ops: this.history() })
    })
  }

  , batch: function({ ops }) {
    ops.forEach(op => { this.receive(op) })
  }

  , genTimestamp: function () {
    this.timestamp = (this.timestamp || this.id << TIMESTAMP_BITS) + 1

    return this.timestamp
  }

  , add: function (op) {
    if (this.index.get(op.t)) { return }

    let prev = this.index.get(op.prev)
      , newNode

    if (!prev) { return this.requestHistory(op) }

    while (prev.next && op.t < prev.next.timestamp) { prev = prev.next }

    newNode = {
      next: prev.next,
      timestamp: op.t,
      chr: op.chr,
      removed: false
    }

    prev.next = newNode
    this.index.set(op.t, newNode)

    return newNode
  }

  , remove: function (op) {
    const node = this.index.get(op.t)

    if (!node) { return this.requestHistory(op) }

    if (node.removed) { return }

    node.removed = true
    return node
  }

  , history: function () {
    let hist = []
      , prev = this.left
      , curr = prev.next

    while (curr) {
      hist.push({
        type: 'add',
        prev: prev.timestamp,
        t: curr.timestamp,
        chr: curr.chr
      });

      if (curr.removed) {
        hist.push({type: 'remove', t: curr.timestamp})
      }

      prev = curr
      curr = curr.next
    }

    return hist
  }
}


// Convert RGA to an array and filter out removed elements
// constructor function for the RGAtoText
function RGAtoText(rga) {
  this.ary = RGA.toArray(rga)
  this.compactedAry =
    this.ary.filter(({removed}) => { return !removed })
}

RGAtoText.prototype = {
  text: function() {
    return this.compactedAry.map(({chr}) => { return chr }).join('')
  }
// Find the index of a node in the compacted array or the index of the previous non-removed node
  , indexOrPrev: function(node) {
    let idx = this.ary.indexOf(node)
    // If the node is removed, find the previous non-removed node
    while (idx >= 0 && node.removed) {
      idx = idx - 1
      node = this.ary[idx]
    }
    // Return the index in the compacted array
    return this.compactedAry.indexOf(node)
  }
// Get the character at a given index in the compacted array
  , get: function(idx) {
    return this.compactedAry[idx]
  }
}


RGA.AceEditorRGA = function AceEditorRGA(id, editor) {
  let rga = new RGA(id)
    , emitContentChanged = true
    , bufferOperations = false
    , operationsBuffer = []

  editor.$blockScrolling = Infinity

  const {session, selection} = editor
    , Doc = session.doc.constructor

  const contentInserted = (from, change) => {
    const ary = new RGAtoText(rga).compactedAry

    let node = ary[from] || rga.left

    change.forEach(chr => {
      node = rga.downstream({
        type: 'add',
        prev: node.timestamp,
        t: rga.genTimestamp(),
        chr: chr
      })
    })
  }

  const contentRemoved = (from, change) => {
    const ary = new RGAtoText(rga).compactedAry

    ary.slice(from, from + change.length).forEach(node => {
      rga.downstream({ type: 'remove', t: node.timestamp })
    })
  }

  const contentChanged = ({ action, start, end, lines }) => {
    if (!emitContentChanged) { return }

    const from = session.doc.positionToIndex(start)
      , change = lines.join("\n").split('')

    if (action === 'insert') {
      contentInserted(from, change)
      // console.log(" from : " + from + " change : "+change) check ok
    } else if (action === 'remove') {
      contentRemoved(from + 1, change)
    }
  }

  let nodeSelection = { startNode: rga.left, endNode: rga.left }
  const cursorChanged = () => {
    if (!emitContentChanged) { return }

    const { start, end } = selection.getRange()
      , rgaAry = new RGAtoText(rga)
      , doc = new Doc(rgaAry.text())
      , startIndex = doc.positionToIndex(start)
      , startNode = rgaAry.get(startIndex)
      , endIndex = doc.positionToIndex(end)
      , endNode = rgaAry.get(endIndex)
    console.log("startNode:", JSON.stringify(startNode), "endNode:", JSON.stringify(endNode));
    nodeSelection = { startNode: startNode, endNode: endNode }
  }

  const syncEditor = _ => {
    emitContentChanged = false

    try {
      const rgaAry = new RGAtoText(rga)
        , text = rgaAry.text()
        , doc = new Doc(text)
        , { startNode, endNode } = nodeSelection
        , startIndex = rgaAry.indexOrPrev(startNode)
        , endIndex  = rgaAry.indexOrPrev(endNode)
        , rangeStart = doc.indexToPosition(startIndex)
        , rangeEnd = doc.indexToPosition(endIndex)
        , range = { start: rangeStart, end: rangeEnd }

      session.doc.setValue(text)
      selection.setSelectionRange(range)
    } finally {
      emitContentChanged = true
    }
  }

  const flushBuffer = () => {
    this.receiveHistory(operationsBuffer)
    bufferOperations = false
    operationsBuffer = []
  }

  const { onCompositionStart, onCompositionEnd } = editor
  editor.onCompositionStart = () => {
    bufferOperations = true
    onCompositionStart.receive(editor, [])
  }

  editor.onCompositionEnd = () => {
    try {
      onCompositionEnd.receive(editor, [])
    } finally {
      setTimeout(flushBuffer, 100)
    }
  }

  // Callbacks
  session.on('change', contentChanged)
  selection.on('changeCursor', cursorChanged)

  // Public interface
  this.receive = (op) => {
    if (bufferOperations) {
      operationsBuffer.push(op)
    } else {
      rga.receive(op)
      syncEditor()
    }
  }

  this.receiveHistory = (history) => {
    history.forEach(op => rga.receive(op))
    syncEditor()
  }

  this.subscribe = (sub) => { rga.subscribe(sub) }
}


// if (typeof module !== 'undefined') { exports = module.exports = RGA }

if (typeof module !== 'undefined') { module.exports = RGA }

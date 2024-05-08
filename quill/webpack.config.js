const path = require('path')

module.exports = {
    mode: 'development',
    devtool: 'source-map',
    entry: {
        quill: './quill.js'
    },
    output: {
        globalObject: 'self',
        path: path.resolve(__dirname, './dist/'),
        filename: '[name].bundle.js',
        publicPath: '/dist/'
    },
    devServer: {
        allowedHosts: 'all',
        port: 3000,
        compress: true,
        static: '.'
    }
}

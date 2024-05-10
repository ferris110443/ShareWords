function loadAsideWorkspace() {
    const html = `
<aside class="d-flex flex-column flex-shrink-0 bg-light" style="width: 6rem;">
  <a href="/admin/home" class="d-block p-0 link-dark text-decoration-none" data-bs-toggle="tooltip" data-bs-placement="right" data-bs-original-title="Icon-only">
<!--    <svg class="bi pe-none" width="40" height="32"><use xlink:href="#bootstrap"></use></svg>-->
    <span class="visually-hidden">Icon-only</span>
    <img id= "shareWords-logo" src=/logo/sharewordslogo.png >   
  </a>
  <ul class="nav nav-pills nav-flush flex-column mb-auto text-center">

    <li>
      <a class="workspace-partial nav-link py-0 border-bottom rounded-0" data-bs-toggle="tooltip" data-bs-placement="right" aria-label="Dashboard" data-bs-original-title="file" data-target="file-zone">
        <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Dashboard"><use xlink:href="#speedometer2"></use></svg>
        <img src= /logo/file.png width="48" height="48">
        <div style="font-size: 14px ">Files</div>
        <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Home"><use xlink:href="#home"></use></svg>
      </a>
    </li>
    <li class="nav-item">
      <a class="workspace-partial nav-link py-0 border-bottom rounded-0" aria-current="page" data-bs-toggle="tooltip" data-bs-placement="right" aria-label="Home" data-bs-original-title="user" data-target="room-zone">
        <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Home"><use xlink:href="#home"></use></svg>
        <img src=/logo/user.png width="48" height="48">
        <div style="font-size: 14px">Members</div>
        <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Home"><use xlink:href="#home"></use></svg>
      </a>
    </li>
<!--    <li>-->
<!--      <a class="workspace-partial nav-link py-0 border-bottom rounded-0" data-bs-toggle="tooltip" data-bs-placement="right" aria-label="Orders" data-bs-original-title="setting" data-target="setting-zone">-->
<!--        <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Orders"><use xlink:href="#table"></use></svg>-->
<!--        <img src=/logo/setting.png width="36" height="36">-->
<!--      </a>-->
<!--    </li>-->
    <li>
      <a href="/admin/home" class="nav-link py-0 border-bottom rounded-0" data-bs-toggle="tooltip" data-bs-placement="right" aria-label="Orders" data-bs-original-title="Orders">
        <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Orders"><use xlink:href="#table"></use></svg>
        <img src=/logo/logout.png width="48" height="48">
        <div style="font-size: 14px">Home</div>
        <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Orders"><use xlink:href="#table"></use></svg>
      </a>
    </li>
  </ul>
</aside>
`;

    const sidebarPlaceholder = document.getElementById('sidebar-placeholder');
    if (sidebarPlaceholder) {
        sidebarPlaceholder.innerHTML = html;
        attachNavLinkListeners(sidebarPlaceholder);
    }
}


function attachNavLinkListeners(container) {
    const navLinks = container.querySelectorAll('.workspace-partial');
    navLinks.forEach(link => {
        link.addEventListener('click', function (event) {
            event.preventDefault();
            const targetSection = this.getAttribute('data-target'); //Room-zone or file-zone

            document.querySelectorAll('.content-section').forEach(section => {
                if (section.id === targetSection) {
                    section.style.display = 'block';

                } else {
                    section.style.display = 'none';
                }
            });
            if (this.getAttribute('data-target') === "room-zone") {
                $("#groupChat-container").css("display", "block");
                $("#workspace-information-container").css("display", "none");
                $("#create-new-file-container").css("display", "none");
            }
            if (this.getAttribute('data-target') === "file-zone") {
                $("#groupChat-container").css("display", "none");
                $("#workspace-information-container").css("display", "block");
                $("#create-new-file-container").css("display", "block");
            }


        });
    });
}

function loadAsideUserHomePage() {
    const html = `
<aside class="d-flex flex-column flex-shrink-0 bg-light" style="width: 6rem;">
    <a href="/admin/home" class="d-block p-0 link-dark text-decoration-none" data-bs-toggle="tooltip" data-bs-placement="right" data-bs-original-title="Icon-only">
<!--      <svg class="bi pe-none" width="40" height="32"><use xlink:href="#bootstrap"></use></svg>-->
      <span class="visually-hidden">Icon-only</span>
      <img id= "shareWords-logo" src=/logo/sharewordslogo.png> 
    </a>
    <ul class="nav nav-pills nav-flush flex-column mb-auto text-center">
      <li class="nav-item">
        <a href="/admin/home" class="nav-link py-0 border-bottom rounded-0" aria-current="page" data-bs-toggle="tooltip" data-bs-placement="right" aria-label="Home" data-bs-original-title="Home">
          <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Home"><use xlink:href="#home"></use></svg>
          <img src=/logo/workspace.png width="48" height="48">
          <div style="font-size: 14px">Workspace</div>
          <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Home"><use xlink:href="#home"></use></svg>

        </a>
      </li>
      <li>
        <a href="/admin/friends" class="nav-link py-0 border-bottom rounded-0" data-bs-toggle="tooltip" data-bs-placement="right" aria-label="Dashboard" data-bs-original-title="Dashboard">
          <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Dashboard"><use xlink:href="#speedometer2"></use></svg>
          <img src=/logo/friends.png width="48" height="48">
          <div style="font-size: 14px">Friends</div>
          <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Home"><use xlink:href="#home"></use></svg>

        </a>
      </li>
      <li>
        <a href="/index.html" class="nav-link py-0 border-bottom rounded-0" data-bs-toggle="tooltip" data-bs-placement="right" aria-label="Orders" data-bs-original-title="Orders">
          <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Orders"><use xlink:href="#table"></use></svg>
          <img src=/logo/logout.png width="48" height="48">
          <div style="font-size: 14px">LogOut</div>
          <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Home"><use xlink:href="#home"></use></svg>

        </a>
      </li>
    </ul>
</aside>
`;

    const sidebarPlaceholder = document.getElementById('user-sidebar-placeholder');
    if (sidebarPlaceholder) {
        sidebarPlaceholder.innerHTML = html;
    }
}

// window.addEventListener('DOMContentLoaded', function () {
//     loadAsideWorkspace();
//     loadAsideUserHomePage();
// });

loadAsideWorkspace();
loadAsideUserHomePage();

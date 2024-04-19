const logoFolderURL = "http://localhost:8080/logo";

function loadAsideWorkspace() {
    const html = `
<aside class="d-flex flex-column flex-shrink-0 bg-light" style="width: 4.5rem;">
  <a href="/templates/home" class="d-block p-3 link-dark text-decoration-none" data-bs-toggle="tooltip" data-bs-placement="right" data-bs-original-title="Icon-only">
    <svg class="bi pe-none" width="40" height="32"><use xlink:href="#bootstrap"></use></svg>
    <span class="visually-hidden">Icon-only</span>
  </a>
  <ul class="nav nav-pills nav-flush flex-column mb-auto text-center">
    <li class="nav-item">
      <a class="nav-link py-0 border-bottom rounded-0" aria-current="page" data-bs-toggle="tooltip" data-bs-placement="right" aria-label="Home" data-bs-original-title="user" data-target="room-zone">
        <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Home"><use xlink:href="#home"></use></svg>
        <img src=${logoFolderURL}/user.png width="36" height="36">
      </a>
    </li>
    <li>
      <a class="nav-link py-0 border-bottom rounded-0" data-bs-toggle="tooltip" data-bs-placement="right" aria-label="Dashboard" data-bs-original-title="file" data-target="file-zone">
        <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Dashboard"><use xlink:href="#speedometer2"></use></svg>
        <img src= ${logoFolderURL}/file.png width="36" height="36">
      </a>
    </li>
    <li>
      <a class="nav-link py-0 border-bottom rounded-0" data-bs-toggle="tooltip" data-bs-placement="right" aria-label="Orders" data-bs-original-title="setting" data-target="setting-zone">
        <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Orders"><use xlink:href="#table"></use></svg>
        <img src=${logoFolderURL}/setting.png width="36" height="36">
      </a>
    </li>
    <li>
      <a class="nav-link py-0 border-bottom rounded-0" data-bs-toggle="tooltip" data-bs-placement="right" aria-label="Orders" data-bs-original-title="Orders">
        <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Orders"><use xlink:href="#table"></use></svg>
        <img src=${logoFolderURL}/logout.png width="36" height="36">
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
    const navLinks = container.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.addEventListener('click', function (event) {
            event.preventDefault(); // Prevent the default anchor behavior

            const targetSection = this.getAttribute('data-target'); // Get the target section from the link


            document.querySelectorAll('.content-section').forEach(section => {
                if (section.id === targetSection) {
                    section.style.display = 'block'; // Show the targeted section
                } else {
                    section.style.display = 'none'; // Hide other sections
                }
            });

        });
    });
}

function loadAsideUserHomePage() {
    const html = `
<aside class="d-flex flex-column flex-shrink-0 bg-light" style="width: 4.5rem;">
    <a href="/admin/home" class="d-block p-3 link-dark text-decoration-none" data-bs-toggle="tooltip" data-bs-placement="right" data-bs-original-title="Icon-only">
      <svg class="bi pe-none" width="40" height="32"><use xlink:href="#bootstrap"></use></svg>
      <span class="visually-hidden">Icon-only</span>
    </a>
    <ul class="nav nav-pills nav-flush flex-column mb-auto text-center">
      <li class="nav-item">
        <a href="/admin/home" class="nav-link active py-0 border-bottom rounded-0" aria-current="page" data-bs-toggle="tooltip" data-bs-placement="right" aria-label="Home" data-bs-original-title="Home">
          <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Home"><use xlink:href="#home"></use></svg>
          <img src=${logoFolderURL}/workspace.png width="36" height="36">
        </a>
      </li>
      <li>
        <a href="/admin/friends" class="nav-link py-0 border-bottom rounded-0" data-bs-toggle="tooltip" data-bs-placement="right" aria-label="Dashboard" data-bs-original-title="Dashboard">
          <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Dashboard"><use xlink:href="#speedometer2"></use></svg>
          <img src=${logoFolderURL}/friends.png width="36" height="36">
        </a>
      </li>
      <li>
        <a href="/admin/logout" class="nav-link py-0 border-bottom rounded-0" data-bs-toggle="tooltip" data-bs-placement="right" aria-label="Orders" data-bs-original-title="Orders">
          <svg class="bi pe-none" width="36" height="36" role="img" aria-label="Orders"><use xlink:href="#table"></use></svg>
          <img src=${logoFolderURL}/logout.png width="36" height="36">
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

window.addEventListener('DOMContentLoaded', function () {
    loadAsideWorkspace();
    loadAsideUserHomePage();
});
<?php

require_once('include/header.php.inc');

?>

    <div id="content" class="content">

        <h1>PCGen Autobuild site</h1>
        <h2>PCGen Development Autobuilds</h2>
        <p>These are regular scheduled builds from the development branch of PCGen. Every four hours, 
        the project is checked for changes and if any have been made a new build is generated.</p>
        <ul>
          <li><a href="http://pcgen.sourceforge.net/autobuilds/">Autobuild report</a>
          <li><a href="https://sourceforge.net/project/showfiles.php?group_id=25576&package_id=191558&release_id=419203">Download Autobuild</a>
        </ul>

        <h2>PCGen Production Autobuilds</h2>
        <p>These are regular scheduled builds from the production branch of PCGen. Every four hours, 
        the project is checked for changes and if any have been made a new build is generated.
        This branch is only active when there is work going on to release a production fix release, 
        or when we are nearing a new full production release.</p>
        <ul>
          <li><a href="http://pcgen.sourceforge.net/autobuilds-prod/">Autobuild report</a>
          <li><a href="https://sourceforge.net/project/showfiles.php?group_id=25576&package_id=191558&release_id=419532">Download Autobuild</a>
        </ul>

    </div> <!-- div content -->

<?php

require_once('include/footer.php.inc');

?>

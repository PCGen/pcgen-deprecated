<?php

require_once('include/header.php.inc');

?>

    <div id="content" class="content">

        <h1>PCGen Autobuild site</h1>
        <p>Autobuilds are compilations of the PCGen program and data taken direct from our source at regular 
        intervals. They are not manually tested at all prior to upload, but are instead an excellent tool to 
        allow people to test what we are currently working on. As a result, unless you like living on the edge, 
        it is best to use one of our regular releases available from the 
        <a href="http://pcgen.sourceforge.net/03_get_pcgen.php">downloads page</a> .</p>
        
        <h2>PCGen Development Autobuilds (5.13.x)</h2>
        <p>These are regular scheduled builds from the development branch of PCGen. Every night, 
        the project is checked for changes and if any have been made a new build is generated.</p>
        <ul>
          <li><a href="http://pcgen.sourceforge.net/autobuilds/">Autobuild report</a>
          <li><a href="http://pcgen.sourceforge.net/autobuilds/download.html">Download Autobuild</a>
        </ul>

        <h2>PCGen Production Autobuilds (5.12.x)</h2>
        <p>These are regular scheduled builds from the production branch of PCGen. Every night, 
        the project is checked for changes and if any have been made a new build is generated.
        This branch is only active when there is work going on to release a production fix release, 
        or when we are nearing a new full production release. The builds currently show any fixes we 
        are doing to the 5.12.0 release.</p>
        <ul>
          <li><a href="http://pcgen.sourceforge.net/autobuilds-prod/">Autobuild report</a>
          <li><a href="http://pcgen.sourceforge.net/autobuilds-prod/download.html">Download Autobuild</a>
        </ul>

    </div> <!-- div content -->

<?php

require_once('include/footer.php.inc');

?>

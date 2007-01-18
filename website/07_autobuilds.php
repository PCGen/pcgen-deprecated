<?php

require_once('include/header.php.inc');

?>

    <div id="content" class="content">

        <h1>PCGen Autobuild site</h1>
        <p>Autobuilds are compilations of the PCGen program and data taken direct from our source at regular 
        intervals. They are not manually tested at all prior to upload, but are instead an excellent tool to 
        allow people to test what we are currently working on. As a result, unless you like living on the edge, 
        it is best to use one of our regular releases available from the 
        <a href="http://pcgen.sourceforge.net/03_downloads.php">downloads page</a> .</p>
        
        <h2>PCGen Development Autobuilds (5.11.x)</h2>
        <p>These are regular scheduled builds from the development branch of PCGen. Every eight hours, 
        the project is checked for changes and if any have been made a new build is generated.
        Note: the files are date stamped so the newer ones are further down the list.</p>
        <ul>
          <li><a href="http://pcgen.sourceforge.net/autobuilds/">Autobuild report</a>
          <li><a href="http://pcgen.sourceforge.net/autobuilds/download.html">Download Autobuild</a>
        </ul>

        <h2>PCGen Production Autobuilds (5.10.x)</h2>
        <p>These are regular scheduled builds from the production branch of PCGen. Every four hours, 
        the project is checked for changes and if any have been made a new build is generated.
        This branch is only active when there is work going on to release a production fix release, 
        or when we are nearing a new full production release. The builds currently show the work we 
        are doing towards the 5.10.2 release. Note: the files are date stamped so the newer ones are 
        further down the list.</p>
        <ul>
          <li><a href="http://pcgen.sourceforge.net/autobuilds-prod/">Autobuild report</a>
          <li><a href="http://pcgen.sourceforge.net/autobuilds-pro/download.html">Download Autobuild</a>
        </ul>

    </div> <!-- div content -->

<?php

require_once('include/footer.php.inc');

?>

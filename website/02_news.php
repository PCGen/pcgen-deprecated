<?php

require_once('include/header.php.inc');

?>

    <div id="content" class="content">

        <h1>News From The Trenches</h1>

        <h2 class="ahem">Nonstandard Web Browser</h2>
        <p class="ahem">It appears that your browser does not comply with the <a href="http://www.w3.org/QA/2002/04/Web-Quality" target="standards" title="More about web standards">W3C web standards</a> which define how web pages are encoded, transmitted, and rendered. This site would look much better in a <a href="http://www.webstandards.org/" target="standards" title="The Web Standards Project">standards-compliant</a> web browser, but its content is <a href="http://www.section508.gov/" title="Section 508 Compliance">accessible</a> to any browser or Internet device.</p>


<?php

writeNewsItems("http://sourceforge.net/export/rss2_projnews.php?group_id=25576&rss_limit=5&rss_fulltext=0");

?>

    </div> <!-- div content -->

<?php

require_once('include/footer.php.inc');

?>

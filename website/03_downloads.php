<?php

require_once('include/header.php.inc');

function writePcgenFileItems($url, $viewProd, $maxRecs)
{
    if ($url)
    {
        $rss = fetch_rss($url);
        $outCount = 0;
        $prevVerNum = array(0,0,0);
        

//        echo "<p>Channel: " . $rss->channel['title'] . "</p>";

        foreach ($rss->items as $item)
        {

//            foreach ($item as $tempkey => $tempvalue)
//            {
//                echo "<p>$tempkey<br />\n" . htmlspecialchars($tempvalue) . "\n</p>\n";
//            }

            $title  = $item['title'];
            $title  = strtolower($title);
            $title  = str_replace("_", " ", $title);
            $title  = str_replace("<br>", "<br /> ", $title);
            $title  = ucwords($title);

            $findme = 'Released';
            $found  = strpos($title, $findme);
            $title  = substr($title, 0, $found);

            $found  = strpos($title, " ");
            $ver    = substr($title, $found);
            $verNum = split("\.", $ver);
            $isProd = false;
            $isSnapshot = false;
            if (count($verNum > 1))
            {
              if ($verNum[1] % 2 == 0)
              {
                if (strpos(strtolower($title), "rc") == 0)
                {
                  $isProd = true;
                }
              }
            }
            if (strpos(strtolower($title), "snapshot") != 0)
            {
            	$isSnapshot = true;
            }

            if ($viewProd && $isProd && ($verNum[0] == $prevVerNum[0]) 
            	&& ($verNum[1] == $prevVerNum[1]))
            {
            	// When showing multiple prod versions, get the latest patch from each
            	$isProd = false;
            }

			if (($outCount < $maxRecs) && !$isSnapshot && (($viewProd && $isProd) || (!$viewProd && !$isProd)))
			{
              $pub    = date("Y-m-d", strtotime($item['pubdate']));

              $desc   = substr($item['description'], 2);
              $findme = '<br>';
              $found  = (4 + strpos($desc, $findme));
              $desc   = substr($desc, $found);
              $desc   = str_replace("<br>", "<br /> ", $desc);
              $desc   = str_replace("),", "),<br /> ", $desc);
              $desc   = str_replace("files:", "files:<br /> ", $desc);
              $desc   = str_replace("&release_id", "&amp;release_id", $desc);

              $href   = $item['link'];
              echo "<h3>$title</h3>\n";

              echo "<div class=\"published\">Released: $pub</div>\n";

              echo "<p>$desc</p>\n";
//              echo "<span class=\"newslink\">$pub</span></p>\n";
              echo "\n";
              $outCount++;
              
              if ($isProd)
              {
              	$prevVerNum = $verNum;
              }
            }
        }
    }
}

?>


    <div id="content" class="content">

        <h1>File Downloads<br />
        <a href="http://sourceforge.net/project/showfiles.php?group_id=25576" style="font-size: 60%;">[View all]</a></h1>
        <p>Standard PCGen file releases are distributed in two packages: a full release including GMGen plugin, and a 
        partial release which includes everything except the GMGen plugin and the PDF libraries.</p>
        <br />

        <h2>Production Release</h2>
        <p>This is the most recent Production PCGen Release. Production releases are stable releases suitable for
        general use. If you are using PCGen data sets from Code Monkey Publishing, you should be using a
        production release of PCGen.
        <a href="http://sourceforge.net/project/showfiles.php?group_id=25576&package_id=129606" style="font-size: 80%;">[View Older Production Releases]</a>
        </p>

<?php

writePcgenFileItems("http://sourceforge.net/export/rss2_projfiles.php?group_id=25576&rss_limit=30", true, 2);

?>
        <br />
        <h2>Alpha and Beta Releases</h2>
        <p>These are development milestone releases designed to display the work in progress on PCGen
        and for use in testing new features. Use at own risk. Basic functionality is tested before
        each alpha and beta build though.
        </p><p>The beta releases have all of the features of the next production release of PCGen, 
        and work focuses on fixing bugs and getting the program ready for a production release.<br />
        <a href="http://sourceforge.net/project/showfiles.php?group_id=25576&package_id=21689" style="font-size: 80%;">[View Older Alpha and Beta Releases]</a>
        </p>

<?php

writePcgenFileItems("http://sourceforge.net/export/rss2_projfiles.php?group_id=25576&rss_limit=20", false, 5);

?>

    </div> <!-- div content -->

<?php

require_once('include/footer.php.inc');

?>

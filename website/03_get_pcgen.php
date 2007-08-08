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

        <h1>How to Get PCGen</h1>
        <P>PCGen is open-source software available for free under the <a href="http://www.gnu.org/copyleft/lgpl.html">LGPL License</a>. There are a couple of ways you can get it:
        <UL>
        <LI><a href="#stable">Stable Download</a></LI>
        <LI><a href="#alpha">Alpha Releases</a></LI>
        <LI><a href="#autobuild">Nightly Builds</a></LI>
        <LI><a href="#subversion">Subversion Access</a></LI>
        </UL>

        <h2>Option 1. Download Latest Stable Release of PCGen<a class="" title="stable" name="stable"></a></h2>
        <p>Click on the link suited to your computer below. You can also look at 
        the <a href="http://sourceforge.net/project/showfiles.php?group_id=25576&package_id=129606" style="font-size: 80%;">Full Package</a> 
        for further files such as PDF documentation and alpha dataasets.         
        </p>
        <p>This is the most recent stable or production PCGen Release. If you are 
        using PCGen data sets from Code Monkey Publishing, you should be using a
        production release of PCGen.
        <a href="http://sourceforge.net/project/showfiles.php?group_id=25576&package_id=129606" style="font-size: 80%;">[View Older Production Releases]</a>
        </p>
        
        <div class="downloadbar"><a href="http://downloads.sourceforge.net/pcgen/pcgen5121_win_install.exe">Download PCGen 5.12.1 for Windows<small>&nbsp;</small></a></div><br />
        <div class="downloadbar"><a href="http://downloads.sourceforge.net/pcgen/pcgen5121_mac_install.dmg">Download PCGen 5.12.1 for Mac<small>&nbsp;</small></a></div><br />
        <div class="downloadbar"><a href="http://downloads.sourceforge.net/pcgen/pcgen5121_full.zip">Download PCGen 5.12.1 for Other Systems<small>&nbsp;</small></a></div><br/>

        <h2>Option 2. Download Alpha Releases<a class="" title="alpha" name="alpha"></a></h2>
        <p>These are development milestone releases designed to display the work in progress on PCGen
        and for use in testing new features. Use at own risk. Basic functionality is tested before
        each alpha and beta build though.
        </p><!--  <p>The beta releases have all of the features of the next production release of PCGen, 
        and work focuses on fixing bugs and getting the program ready for a production release.<br /> -->
        <a href="http://sourceforge.net/project/showfiles.php?group_id=25576&package_id=21689" style="font-size: 80%;">[View Older Alpha and Beta Releases]</a>
        </p>
<?php

writePcgenFileItems("http://sourceforge.net/export/rss2_projfiles.php?group_id=25576&rss_limit=20", false, 5);

?>

        <h2>Option 3. Nightly Builds<a class="" title="autobuild" name="autobuild"></a></h2>
        <p>Autobuilds are compilations of the PCGen program and data taken 
        direct from our source at regular intervals. They are not manually tested 
        at all prior to upload, but are instead an excellent tool to allow people 
        to test what we are currently working on.
        </p><p>See the 
        <a href="07_autobuilds.php" style="font-size: 80%;">Autobuilds page</a>
        for these downloads.
        </p>

        <h2>Option 4. Subversion Access<a class="" title="subversion" name="subversion"></a></h2>
        <p>We use the Subversion service hosted by SourceForge for 
        all our code and data. If you want direct acces to the source, see the 
        <a href="http://pcgen.sourceforge.net/autobuilds/scm-usage.html" style="font-size: 80%;">Subversion usage page</a>
        </p>

    </div> <!-- div content -->

<?php

require_once('include/footer.php.inc');

?>

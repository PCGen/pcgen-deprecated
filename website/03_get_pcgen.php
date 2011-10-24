<?php

require_once('include/header.php.inc');

function nth_position($str, $letter, $n, $offset = 0)
{
    $str_arr = str_split($str);
    $letter_size = array_count_values(str_split(substr($str, $offset)));
    if( !isset($letter_size[$letter]))
	{
        trigger_error('letter "' . $letter . '" does not exist in ' . $str . ' after ' . $offset . '. position', E_USER_WARNING);
        return false;
    }
	else if($letter_size[$letter] < $n)
	{
        trigger_error('letter "' . $letter . '" does not exist ' . $n .' times in ' . $str . ' after ' . $offset . '. position', E_USER_WARNING);
        return false;
    }
    for($i = $offset, $x = 0, $count = (count($str_arr) - $offset); $i < $count, $x != $n; $i++)
	{
        if($str_arr[$i] == $letter)
		{
            $x++;
        }
    }
    return $i - 1;
}

function writePcgenFileItems($url, $viewProd, $maxRecs)
{
    if ($url)
    {
        $rss = fetch_rss($url);
        $outCount = 0;
        $prevVerNum = array(0,0,0);

        $ver2 = '';
        foreach ($rss->items as $item)
		{
            $title  = $item['title'];
            $title  = strtolower($title);
            $title  = str_replace("_", " ", $title);
            $title  = str_replace("<br>", "<br /> ", $title);
            $title  = ucwords($title);
			$found = nth_position($title, '/', 2);
			$found2 = nth_position($title, '/', 3);
			$ver    = substr($title, $found + 1, $found2 - $found - 1);
			if ($ver != $ver2)
			{
			    $ver2 = $ver;
				$display = true;
			}
			else
			{
				$display = false;
			}

			// Check its PRD status
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

			if (($outCount < $maxRecs) && !$isSnapshot && (($viewProd && $isProd) || (!$viewProd && !$isProd)) && $display)
			{
                $pub    = date("Y-m-d", strtotime($item['pubdate']));
				$href   = 'http://sourceforge.net/projects/pcgen/files/PCGen%20Unstable/';
				$ver = str_replace("Rc", "RC", $ver);
	    	    echo "<h3><a href='$href$ver'>$ver</a></h3>\n";
	    	    echo "Released: $pub\n";
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
        <p>PCGen is open-source software available for free under the <a href="http://www.gnu.org/copyleft/lgpl.html">LGPL License</a>. There are a couple of ways you can get it:
        <ul>
            <li><a href="#stable">Stable Download</a></li>
            <li><a href="#data">Stable Data Sets</a> - New data sets can be installed into an existing version of PCGen</li>
            <li><a href="#alpha">Alpha/Beta/RC Releases</a></li>
            <li><a href="#autobuild">Nightly Builds</a></li>
            <li><a href="#subversion">Subversion Access</a></li>
            <li><a href="http://wiki.pcgen.org/index.php?title=Roadmap">Roadmap</a> - Find out when you get your next fix!</li>
            <li><a href="http://sourceforge.net/projects/pcgen/files/PrettyLst/v%201.39%20build%208180/prettylst_1-39_build-8180.zip/download" target="_blank">PrettyLst</a> - PERL Utility for data coders.</li>
        </ul>

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

        <div class="downloadbar"><a href="http://downloads.sourceforge.net/pcgen/pcgen5164_win_install.exe">Download PCGen 5.16.4 for Windows<small>&nbsp;</small></a></div><br />
        <div class="downloadbar"><a href="http://downloads.sourceforge.net/pcgen/pcgen5164_mac_build.dmg">Download PCGen 5.16.4 for Mac<small>&nbsp;</small></a></div><br />
        <div class="downloadbar"><a href="http://downloads.sourceforge.net/pcgen/pcgen5164_full.zip">Download PCGen 5.16.4 for Other Systems<small>&nbsp;</small></a></div><br/>

        <h3>Download Stable Data Sets<a class="" title="data" name="data"></a></h3>
        <p>These are stable data sets that are developed in between stable releases of PCGen but can be installed and used with a stable version of PCGen.
        </p>
        <div class="downloadbar"><a href="https://sourceforge.net/projects/pcgen/files/PCGen%20Stable%20Datasets/5.16.4%20OOC%20Data%20Sets/5164_piazo_pathfinder_alpha_update_07.pcz/download">Download Pathfinder RPG APG dataset Update 7 for PCGen 5.16.4</a></div><br />
        <p>
        <a href="http://sourceforge.net/projects/pcgen/files/PCGen%20Stable%20Datasets/" style="font-size: 80%;">[View Stable Data Sets]</a>
        </p>

        <h2>Option 2. Download Alpha, Beta and RC Releases<a class="" title="alpha" name="alpha"></a></h2>
        <p>These are development milestone releases designed to display the work in progress on PCGen
        and for use in testing new features. Use at own risk. Basic functionality is tested before
        each alpha and beta build though.
        </p>
<?php
writePcgenFileItems("http://sourceforge.net/api/file/index/project-id/25576/mtime/desc/rss?path=/PCGen%20Unstable", false, 5);
?>
		<p>
        <a href="http://sourceforge.net/project/showfiles.php?group_id=25576&package_id=21689" style="font-size: 80%;">[View Older Alpha and Beta Releases]</a>
        </p>

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
        all our code and data. If you want direct access to the source, see the
        <a href="http://sourceforge.net/scm/?type=svn&group_id=25576" style="font-size: 80%;">Subversion usage page</a>
        </p>

    </div> <!-- div content -->

<?php

require_once('include/footer.php.inc');

?>

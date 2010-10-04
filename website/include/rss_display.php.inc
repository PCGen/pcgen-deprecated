<?php

function writeNewsItems($url)
{
    if ($url)
    {
        $rss = fetch_rss($url);

//        echo "<p>Channel: " . $rss->channel['title'] . "</p>";

        foreach ($rss->items as $item)
        {
            $title  = htmlspecialchars($item['title']);
            $title  = str_replace("<br>", "<br /> ", $title);
            $pub    = date("Y-m-d", strtotime($item['pubdate']));
            $desc   = str_replace("<br>", "<br /> ", $item['description']);
            $href   = $item['link'];

            echo "<h2>$title</h2>\n";

            echo "<div class=\"published\">Published: $pub</div>\n";

            echo "<p>$desc \n";
            echo "<span class=\"newslink\"><a href=\"$href\" title=\"$title\">[Read More]</a></span></p>\n";
            echo "\n";
        }
    }
}

function writeDocItems($url)
{
    if ($url)
    {
        $rss = fetch_rss($url);

//        echo "<p>Channel: " . $rss->channel['title'] . "</p>";

        foreach ($rss->items as $item)
        {

//            foreach ($item as $tempkey => $tempvalue)
//            {
//                echo "<p>$tempkey<br />\n" . htmlspecialchars($tempvalue) . "\n</p>\n";
//            }

            $title  = $item['title'];
//            $title  = strtolower($title);
            $title  = str_replace("_", " ", $title);
            $title  = str_replace("<br>", "<br /> ", $title);
            $title  = ucwords($title);
            $findme = ' (DocID';
            $found  = strpos($title, $findme);
            $title  = substr($title, 0, $found);

            $pub    = date("Y-m-d", strtotime($item['pubdate']));

            $desc   = $item['description'];
            $findme = 'Description:';
            $found  = (strpos($desc, $findme));
            $link   = substr($desc, 0, $found);
            $found  = (12 + strpos($desc, $findme));
            $desc   = substr($desc, $found);

            $findme = ', document originally created';
            $found  = (strpos($desc, $findme));
            $desc   = substr($desc, 0, $found) . ')';

            $desc   = str_replace("<br>", "<br /> ", $desc);

            $href   = $item['link'];

            echo "<h2>$title</h2>\n";

            echo "<div class=\"published\">Released: $pub</div>\n";

            echo "<p>$desc $link</p>\n";
//            echo "<span class=\"newslink\">$pub</span></p>\n";
            echo "\n";
        }
    }
}

function writeFileItems($url)
{
    if ($url)
    {
        $rss = fetch_rss($url);

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

            $pub    = date("Y-m-d", strtotime($item['pubdate']));

            $desc   = substr($item['description'], 4);
            $findme = '<br>';
            $found  = (4 + strpos($desc, $findme));
            $desc   = substr($desc, $found);
            $desc   = str_replace("<br>", "<br /> ", $desc);
            $desc   = str_replace("&release_id", "&amp;release_id ", $desc);

            $href   = $item['link'];

            echo "<h2>$title</h2>\n";

            echo "<div class=\"published\">Released: $pub</div>\n";

            echo "<p>$desc</p>\n";
//            echo "<span class=\"newslink\">$pub</span></p>\n";
            echo "\n";
        }
    }
}

function writeStatItems($url)
{
    if ($url)
    {
        $rss = fetch_rss($url);

//        echo "<p>Channel: " . $rss->channel['title'] . "</p>";

        foreach ($rss->items as $item)
        {
            $title  = $item['title'];
            if (strpos(strtolower($title), "activity percentile") === 0)
            {
                $title  = substr($title, 32);
                echo "Activity: $title<br />\n";
            }
            else if (strpos(strtolower($title), "discussion forums (public):") === 0)
            {
                $title  = substr($title, 27);
                echo "Forums: $title<br />\n";
            }
            // Karianna 26/04/2010 - comment out deprecated SF tracker RSS feeds
            /*
            else if (strpos(strtolower($title), "tracker: bugs") === 0)
            {
                $title  = substr($title, 13);
                echo "Bugs: $title<br />\n";
            }
            else if (strpos(strtolower($title), "tracker: feature requests") === 0)
            {
                $title  = substr($title, 25);
                echo "Feature Requests: $title<br />\n";
            }
            else if (strpos(strtolower($title), "tracker: support requests") === 0)
            {
                $title  = substr($title, 25);
                echo "Support Requests: $title<br />\n";
            }
            */
        }
    }
}

?>

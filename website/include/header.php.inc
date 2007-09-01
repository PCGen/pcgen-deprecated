<?php

define('MAGPIE_CACHE_DIR', '/tmp/persistent/pcgenweb/cache');
define('MAGPIE_FETCH_TIME_OUT', 60);

require_once('include/rss_fetch.php.inc');
require_once('include/rss_display.php.inc');

$title = basename($_SERVER['PHP_SELF'], '.php');

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1DTD/xhtml1-transitional.dtd">
<html lang="en">
<head>
    <title>PCGen :: <?php echo ucwords(substr(str_replace('_', ' ', $title), 3)); ?></title>

    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <meta name="keywords" content="pcgen, open, source, oss, character, generator, java, chargen, dnd, d&amp;d, d20, ogl" />
    <meta name="description" content="PCGen is an open source character generator for role-playing games. It currently is aimed at supporting as many RPGs as humanly possible." />

    <style type="text/css" media="print">@import "styles/print.css";</style>
    <style type="text/css" media="screen" title="Vellum">@import "styles/vellum.css";</style>
    <link rel="alternate stylesheet" type="text/css" href="styles/print.css" title="Printer Friendly" />

</head>
<body>

<div id="pagehead">
    <h1>PCGen</h1>
    <p>An open source RPG character generator</p>
</div> <!-- div pagehead -->

<div id="pagemenu">
    <ul>
        <li><a href="http://pcgen.wiki.sourceforge.net">Project Wiki</a></li>
        <li><a href="http://groups.yahoo.com/group/pcgen">Main Yahoo Group</a></li>
        <li><a href="https://sourceforge.net/project/project_donations.php?group_id=25576">Donate!</a></li>
        <li><a href="https://sourceforge.net/account/login.php">SF Login</a></li>
        <li><a href="https://sourceforge.net/projects/pcgen/">SF Project Page</a></li>
        <li><a href="https://sourceforge.net/tracker/?atid=384719&amp;group_id=25576&amp;func=browse" title="You must log into SourceForge in order to report a bug.">Report Bug</a></li>
        <li><a href="https://sourceforge.net/tracker/?atid=384722&amp;group_id=25576&amp;func=browse" title="You must log into SourceForge in order to request a new feature.">Request Feature</a></li>
        <li><a href="http://pcgen.svn.sourceforge.net/viewcvs.cgi/pcgen/" title="SourceForge SVN Repository">SVN</a></li>
    </ul>
</div> <!-- div pagemenu -->

<div id="pagebody">

    <div id="tabmenu">
        <ul>

<?php

if ($handle = opendir('.'))
{
    while (false !== ($file = readdir($handle)))
    {
        if (substr($file,0,1) != "." && strlen($file.length) && substr($file,-4) == ".php" && !is_dir($file))
        {
            $tabs[] = basename($file, '.php');
        }
    }
    closedir($handle);

    sort($tabs);
}
else
{
    echo 'Directory not readable. Script exiting.' . "\n";
    exit;
}

// $tabs = array('news', 'overview', 'downloads', 'support', 'email_lists', 'trackers');
reset ($tabs);

foreach ($tabs as $tab)
{
    if (strtolower($tab) != 'index')
    {
        $tabLabel = ucwords(substr(str_replace('_', ' ', $tab), 3));

        if (strtolower($title) == strtolower($tab))
        {
            echo '<li><span class="current">' . $tabLabel . '</span></li>' . "\n";
        }
        else
        {
            echo '<li><a href="' . $tab . '.php">' . $tabLabel . '</a></li>' . "\n";
        }
    }
}

?>

        </ul>
    </div> <!-- div tabs -->

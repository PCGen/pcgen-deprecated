<?php

if ($handle = opendir('.'))
{
    while (false !== ($file = readdir($handle)))
    {
        if (substr($file,0,1) != "." && strlen($file.length) && substr($file,-4) == ".php" && !is_dir($file))
        {
            $tabs[] = basename($file);
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

reset ($tabs);

$locationServer = $_SERVER['HTTP_HOST'] . dirname($_SERVER['PHP_SELF']);

header("Location: http://" . $locationServer . $tabs[0]);

?>

PCGen
=====

PCGen - Open Source character generator for role-playing games

* PCGen is a FREE Open Source character generator and maintenance program for role-playing games. 
* It currently supports the d20 RPG system and includes data sets based upon gaming material from Wizards of the Coast, Paizo Publishing and dozens of other publishers. 
* The project's current focus is on gaming material released under Wizards of the Coast's Open Gaming License (OGL) and sources supporting the Pathfinder RPG by Paizo, but the PCGen team also works with publishers to get permission to include limited Closed Content as well. 
* PCGen runs on Windows, Mac OS X and Unix/Linux using Java

The main website of PCGen is:
http://pcgen.sourceforge.net/


How to use this?
================

First off, please download if you just need to create a character. This will get you the latest version that is stable and supported. If you want to help develop PCGen, you are at the right place.

What is what?
-------------

* pcgen/ - contains the Java program that is PCGen
* pcgen/data/ - contains the LST files (list files) that make PCGen understand D20 rules
* website/ - the PCGen website
* utilities/ - various utilities that developers use
* content/ - stuff that is not in the program, but that we are working on
* content/notfordistribution/ - very early versions of D20 rule datasets
* architecture/ - documentation on the architecture


How do I contribute?
====================

1. Please clone this repository on Github. 
2. Create a descriptive branch
   * If you are working on adding a new source: `add-goblins-of-golarion`
   * If you are working on links in the outputsheets: `add-links-to-OS`
   * If you are just cleaning up docs: `cleaning-up-docs`
   * If you are preparing 6.1.2: `prepare-6.1.2-for-release`
3. Then hack on the program, the LST files, hack on new sets or the site.
4. Create a pull request using the button on Github.



How to compile PCGen?
=====================

1. Install the prerequisites:

On Debian/Ubuntu:

    ```bash
    apt-get install openjdk-6-jdk ant
    apt-get install subversion git
    ```

On MacOSX, Ant is installed, Java is also installed. Please get GIT or SVN. 


2. Get the sources from the PCGen subversion or from github:

    ```bash
    # subversion
    svn checkout svn://svn.code.sf.net/p/pcgen/code/Trunk
    ```

    ```bash
    # github
    git clone https://github.com/pcgen/pcgen
    ```

3. Build the sources in the PCGen directory:

    ```bash
    cd pcgen
    ant build
    ```

4. Enjoy the latest and/or greatest

   ```bash
   ./pcgen.sh
   ```

As it becomes necessary for library files to be delivered with PCGen, this is the directory where those libraries should be placed. It is our goal to make these libraries optional whenever possible.
If a jar has dependencies, please try to place it's dependencies in the same subdirectory as the jar.

Currently included are:
emma/emma.jar -- Code coverage tool. There is an ant task that runs it. http://emma.sourceforge.net/ TODO Move this into the test directory.
emma/emma_ant.jar -- Ant tasks for emma. http://emma.sourceforge.net/ TODO Move this into the test directory.

fop/avalon-framework-cvs-20020806.jar -- from fop 0.20.5 distribution
fop/batik.jar -- from fop 0.20.5 distribution, used by fop
fop/fop.jar -- fop 0.20.5
fop/xalan-2.5.2.jar -- from fop 0.20.5 distribution, used by fop
fop/xercesImpl-2.5.0.jar -- from fop 0.20.5 distribution, used by fop
fop/xml-apis.jar -- from fop 0.20.5 distribution, used by fop

jep/djep-2.24.jar -- Used by Gmgen. Adds dice functionality to JEP. TODO Should probably be removed and replaced with JEP plugins.
jep/jep-2.3.1.jar -- "Java Expression Parser" http://www.singularsys.com/jep/ (GPL project, but we have a license exception. See the jep/jep.LICENSE.EXCEPTIONS.txt file.)
jep/RngPack-1.1a.jar -- RngPack http://www.honeylocust.com/RngPack/ used by JEP/PJEP

skinlf.jar -- Skinnable look and feel 6.2, skinnable java look and feel. Pcgen has explicit code support for it. http://skinlf.l2fprod.com/
../lnf/themes/*themepack.zip -- Themepacks used by skinlf.jar
kunststoff.jar -- Kunststoff Look and Feel 2.0.2, optional java look and feel. Pcgen has explicit code support for it.
wraplf.jar -- Used to support anti-aliasing on jdk < 5.0, seems to not support mac? http://wraplf.l2fprod.com/

test/clover.jar -- Code coverage tool, not open source, we have a license. There is an ant task that runs it. http://www.cenqua.com/clover/
test/junit-4.1.jar -- Java Unit test 4.1, used by test classes. http://junit.org/
test/xmlunit1.0.jar -- Used by some junit tests. http://xmlunit.sourceforge.net/

javacc.jar -- Java Compiler Compiler 3.2, used by 'dice rolling' functions; only needed for development. https://javacc.dev.java.net/
jdom.jar -- JDOM is used all over the GMGen project. http://jdom.org
MRJ141Stubs.jar -- MRJ Adapter http://www.roydesign.net/mrjadapter/ Used by Gmgen
readme.txt -- this file
*.LICENSE.txt -- The license for the jar in question, or the jars included in that jar, provided the license is not in the jar.

-PCGen Development Team

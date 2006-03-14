;This Script will take the PCGen Full Zip and prep it for
;for the installer script

For PDF:

lib\fop.jar
lib\fop.LICENSE.txt
lib\jdom.jar
lib\jdom.LICENSE.txt
lib\xml-apis.jar
lib\xml-apis.README.txt

For Swing (the screen skins):

Everything in the lib directory except fop.jar, jdom.jar, jep.jar, xml-apis.jar

GMGEN:

Everything in the plugin directory except CharacterSheet.jar.

Normally, pcgen_partial is only CharacterSheet.jar in the plug-in directory and only jep.jar (and the license file) in the lib directory.


; WINRAR
rar e '*.rar' '*.asm'
; Move File
Set objFSO = CreateObject("Scripting.FileSystemObject")
objFSO.MoveFile "C:\FSO\ScriptLog.log" , "D:\Archive"
; Move Files
Set objFSO = CreateObject("Scripting.FileSystemObject")
objFSO.MoveFile "C:\FSO\*.txt" , "D:\Archive\"
;Move Folder
Set objFSO = CreateObject("Scripting.FileSystemObject")
objFSO.MoveFolder "C:\Scripts" , "M:\helpdesk\management"
;Delete a File
Set objFSO = CreateObject("Scripting.FileSystemObject")
objFSO.DeleteFile("C:\FSO\ScriptLog.txt")


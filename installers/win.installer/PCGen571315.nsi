; Script generated with the Venis Install Wizard
;
; Script Created with Venis IX 2.2.3
; 										NSIS 2.04
;
; Known issues
; 1.5	PCGen install directory is not romoved under full uninstall
;			Need file association for .pcg files
;
;
; Version history
; 1.0 First version uploaded to CVS
; 1.1 Updated PDF Plug-in to only copy PDF output templates if PDF is selected
; 1.2 Pointed the desktop shortcut to pcgen_high_mem.bat and added start /min to batches
;	1.3 Changed path to force ${APPDIR} should solve problems with install directory (fail)
; 1.4 Pointed shortcuts to icons in local, created internal version
; 1.5 Changed location of uninstaller to fix full uninstall problem

; Define constants
!define APPNAME "PCGen"
!define APPNAMEANDVERSION "PCGen 5.7.13"
!define APPDIR "PCGen5713"
!define INTVER "1.5"
!define TargetVer "1.4"
!define OverVer "1.5"
!define OutName "setup_${INTVER}_pcgen5713_win"

; Main Install settings
Name "${APPNAMEANDVERSION}"
InstallDir "$PROGRAMFILES\${APPNAME}"
InstallDirRegKey HKLM "Software\${APPNAME}\${APPDIR}" ""
OutFile "C:\Documents and Settings\Lisa\Desktop\${OutName}.exe"
SetCompressor lzma
CRCCheck on

; Install Type Settings
InstType "Full Install"
InstType "Average Install"
InstType "Average All SRD"
InstType "Min - SRD"
InstType "Min - SRD 3.5"
InstType "Min - MSRD"

;	Look and style
ShowInstDetails show
InstallColors FF8080 000030
XPStyle on
Icon "D:\@Download\PCGen\Local\PCGen2.ico"

; Modern interface settings
!include "MUI.nsh"

!define MUI_ABORTWARNING

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "D:\@Download\PCGen\PCGen_5713b\docs\acknowledgments\PCGenLicense.txt"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

; Set languages (first is default language)
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_RESERVEFILE_LANGDLL

; Set User Variables
	Var "JREVer"
	Var "JDKVer"
	
Function .onInit
	
	;Checks for versions of Java between TargetVer and OverVer
	; this can be changed once PCGen is stable on Java 1.5 and above

  ClearErrors
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
	StrCpy $JREVer $R0
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R0" "JavaHome"
  IfErrors 0 FoundVM

  ClearErrors
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
	StrCpy $JDKVer $R0
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$R0" "JavaHome"
  IfErrors 0 FoundVM

  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  IfErrors 0 FoundVM
	
	DetailPrint "Java not found"
	Sleep 800
  MessageBox MB_ICONEXCLAMATION|MB_YESNO \
                    'Could not find a Java Runtime Environment installed on your computer. \
                     $\nWithout it you cannot run "${APPNAME}". \
                     $\n$\nWould you like to visit the Java website to download it?' \
                    IDNO End
  ExecShell open "http://java.com/en/download/windows_automatic.jsp"
	Goto End

  FoundVM:
	DetailPrint "Java was found"
	DetailPrint $JREVer
	DetailPrint $JDKVer
	IntCmp $JREVer ${TargetVer} VMGood VMOld VMOver
	IntCmp $JDKVer ${TargetVer} VMGood VMOld VMOver
	Goto Error
	
	VMOver:
	IntCmp $JREVer ${OverVer} VMHigh VMGood VMHigh
	IntCmp $JDKVer ${OverVer} VMHigh VMGood VMHigh
	Goto Error
	
	VMHigh:
	DetailPrint "Java Version Bad"
	Sleep 800
  MessageBox MB_ICONEXCLAMATION|MB_YESNO \
                    'Found Java Runtime Environment installed on your computer. \
                     $\nVersion was not "${TargetVer}". \
                     $\n$\nWould you like to visit the Java website to download newest?' \
                    IDNO Error
  ExecShell open "http://java.com/en/download/windows_automatic.jsp"
	Goto Error
	
	VMGood:
	DetailPrint "Java Version Good"
	Goto End
	
	VMOld:
	DetailPrint "Java Version Bad"
	Sleep 800
  MessageBox MB_ICONEXCLAMATION|MB_YESNO \
                    'Found Java Runtime Environment installed on your computer. \
                     $\nVersion was not "${TargetVer}". \
                     $\n$\nWould you like to visit the Java website to download newest?' \
                    IDNO Error
  ExecShell open "http://java.com/en/download/windows_automatic.jsp"
	Goto Error
	
	Error:
	MessageBox MB_YESNO|MB_ICONQUESTION "PCGen will most likely not run, do you wish to install it anyway?" IDYES End
	Abort "Error during Java Detection"

	End:
	
 FunctionEnd

Section "PCGen" Section1

	SectionIn RO
	
	; Set Section properties
	SetOverwrite ifnewer

	; Set Section Files and Shortcuts
	SetOutPath "$INSTDIR\${APPDIR}\"
	File /r "D:\@Download\PCGen\PCGen_5713b\*.*"
	
SectionEnd

SubSection /e "Data" Section2

SubSection "Alpha"
	
	Section "Alderac Entertainment Group"

	SectionIn 1
	
	SetOutPath "$INSTDIR\${APPDIR}\data\alpha\alderacentgroup\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\alpha\alderacentgroup\*.*"
	
	SectionEnd
	
	Section "Avalanch Press"

	SectionIn 1

	SetOutPath "$INSTDIR\${APPDIR}\data\alpha\avalanchepress\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\alpha\avalanchepress\*.*"
	
	SectionEnd
	
	Section "Bastion Press"

	SectionIn 1

	SetOutPath "$INSTDIR\${APPDIR}\data\alpha\bastionpress\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\alpha\bastionpress\*.*"

	SectionEnd

	Section "Dog House Rules"

	SectionIn 1

	SetOutPath "$INSTDIR\${APPDIR}\data\alpha\doghouserules\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\alpha\doghouserules\*.*"

	SectionEnd

	Section "Fantasy Flight Games"

	SectionIn 1
	
	SetOutPath "$INSTDIR\${APPDIR}\data\alpha\fantasyflightgames\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\alpha\fantasyflightgames\*.*"

	SectionEnd

	Section "Green Ronin Publishing"

	SectionIn 1
	
	SetOutPath "$INSTDIR\${APPDIR}\data\alpha\greenronin\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\alpha\greenronin\*.*"

	SectionEnd

	Section "Mongoose"

	SectionIn 1
	
	SetOutPath "$INSTDIR\${APPDIR}\data\alpha\mongoose\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\alpha\mongoose\*.*"

	SectionEnd
	
	Section "Panda Head"

	SectionIn 1
	
	SetOutPath "$INSTDIR\${APPDIR}\data\alpha\pandahead\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\alpha\pandahead\*.*"

	SectionEnd
	
	Section "Pinnacle Entertainment"

	SectionIn 1
	
	SetOutPath "$INSTDIR\${APPDIR}\data\alpha\pinnacleentertainment\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\alpha\pinnacleentertainment\*.*"

	SectionEnd

	Section "RPG Objects"

	SectionIn 1

	SetOutPath "$INSTDIR\${APPDIR}\data\alpha\rpgobjects\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\alpha\rpgobjects\*.*"

	SectionEnd

	Section "Soverign Press"

	SectionIn 1

	SetOutPath "$INSTDIR\${APPDIR}\data\alpha\sovereignpress\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\alpha\sovereignpress\*.*"

	SectionEnd

SubSectionEnd

SubSection "d20OGL"

	Section "Alderac Entertainment Group"

	SectionIn 1 2
	
	SetOutPath "$INSTDIR\${APPDIR}\data\d20ogl\alderacentertainmentgroup\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\d20ogl\alderacentertainmentgroup\*.*"
	
	SectionEnd
	
	Section "Avalanch Press"

	SectionIn 1 2
	
	SetOutPath "$INSTDIR\${APPDIR}\data\d20ogl\avalanchepress\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\d20ogl\avalanchepress\*.*"
	
	SectionEnd
	
	Section "Bastion Press"

	SectionIn 1 2

	SetOutPath "$INSTDIR\${APPDIR}\data\d20ogl\bastionpress\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\d20ogl\bastionpress\*.*"

	SectionEnd

	Section "Battlefield Press"

	SectionIn 1 2

	SetOutPath "$INSTDIR\${APPDIR}\data\d20ogl\battlefieldpress\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\d20ogl\battlefieldpress\*.*"

	SectionEnd

	Section "Creative Mountain Games"

	SectionIn 1 2

	SetOutPath "$INSTDIR\${APPDIR}\data\d20ogl\creativemountaingames\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\d20ogl\creativemountaingames\*.*"

	SectionEnd

	Section "Fantasy Community Council"

	SectionIn 1 2

	SetOutPath "$INSTDIR\${APPDIR}\data\d20ogl\fantasycommunitycouncil\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\d20ogl\fantasycommunitycouncil\*.*"

	SectionEnd

	Section "Fantasy Flight Games"

	SectionIn 1 2
	
	SetOutPath "$INSTDIR\${APPDIR}\data\d20ogl\fantasyflightgames\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\d20ogl\fantasyflightgames\*.*"

	SectionEnd

	Section "Green Ronin Publishing"

	SectionIn 1 2
	
	SetOutPath "$INSTDIR\${APPDIR}\data\d20ogl\greenronin\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\d20ogl\greenronin\*.*"

	SectionEnd

	Section "Malhavoc Press"

	SectionIn 1 2
	
	SetOutPath "$INSTDIR\${APPDIR}\data\d20ogl\malhavocpress\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\d20ogl\malhavocpress\*.*"

	SectionEnd
	
	Section "Mongoose"

	SectionIn 1 2
	
	SetOutPath "$INSTDIR\${APPDIR}\data\d20ogl\mongoosepublishing\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\d20ogl\mongoosepublishing\*.*"

	SectionEnd
	
	Section "MSRD"

	SectionIn 1 2 3 6
	
	SetOutPath "$INSTDIR\${APPDIR}\data\d20ogl\msrd\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\d20ogl\msrd\*.*"

	SectionEnd
	
	Section "SRD"

	SectionIn 1 2 3 4
	
	SetOutPath "$INSTDIR\${APPDIR}\data\d20ogl\srd\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\d20ogl\srd\*.*"

	SectionEnd

	Section "SRD 3.5"

	SectionIn 1 2 3 5

	SetOutPath "$INSTDIR\${APPDIR}\data\d20ogl\srd35\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\d20ogl\srd35\*.*"

	SectionEnd

	Section "Sword and Sorcery Studios"

	SectionIn 1 2

	SetOutPath "$INSTDIR\${APPDIR}\data\d20ogl\swordandsorcerystudios\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\d20ogl\swordandsorcerystudios\*.*"
	
	SectionEnd

SubSectionEnd

SubSection "Permissioned"
	
	Section "Alderac Entertainment Group"

	SectionIn 1
	
	SetOutPath "$INSTDIR\${APPDIR}\data\permissioned\alderacentertainmentgroup\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\permissioned\alderacentgroup\*.*"
	
	SectionEnd
	
	Section "Atlas Games"

	SectionIn 1
	
	SetOutPath "$INSTDIR\${APPDIR}\data\permissioned\atlasgames\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\permissioned\atlasgames\*.*"
	
	SectionEnd
	
	Section "Auran DnD"

	SectionIn 1
	
	SetOutPath "$INSTDIR\${APPDIR}\data\permissioned\aurand20\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\permissioned\aurand20\*.*"
	
	SectionEnd
	
	Section "Avalanche Press"

	SectionIn 1
	
	SetOutPath "$INSTDIR\${APPDIR}\data\permissioned\avalanchepress\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\permissioned\avalanchepress\*.*"
	
	SectionEnd
	
	Section "Green Ronin Publishing"

	SectionIn 1
	
	SetOutPath "$INSTDIR\${APPDIR}\data\permissioned\greenronin\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\permissioned\greenronin\*.*"
	
	SectionEnd
	
	Section "Malhavoc Press"

	SectionIn 1
	
	SetOutPath "$INSTDIR\${APPDIR}\data\permissioned\malhavocpress\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\permissioned\malhavocpress\*.*"
	
	SectionEnd
	
	Section "Mongoose"

	SectionIn 1
	
	SetOutPath "$INSTDIR\${APPDIR}\data\permissioned\mongoose\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\permissioned\mongoose\*.*"
	
	SectionEnd
	
	Section "RPG Objects"

	SectionIn 1
	
	SetOutPath "$INSTDIR\${APPDIR}\data\permissioned\rpgobjects\"
	File /r "D:\@Download\PCGen\PCGen_5713c\data\permissioned\rpgobjects\*.*"
	
	SectionEnd
	
SubSectionEnd

SubSectionEnd

SubSection /e "PlugIns" Section3
	
	Section "Skins"
	
	SectionIn 1 2 3
	SetOutPath "$INSTDIR\${APPDIR}\lib"
	File /r "D:\@Download\PCGen\PCGen_5713c\plugin\skin\lib\*.*"
		
	SectionEnd
	
	Section "PDF"
	
	SectionIn 1 2 3
	SetOutPath "$INSTDIR\${APPDIR}\lib"
	File /r "D:\@Download\PCGen\PCGen_5713c\plugin\pdf\lib\*.*"
	SetOutPath "$INSTDIR\${APPDIR}\outputsheets"
	File /r "D:\@Download\PCGen\PCGen_5713c\plugin\pdf\outputsheets\*.*"
	
	SectionEnd
	
	Section "GMGen"
	
	SectionIn 1 2 3
	SetOutPath "$INSTDIR\${APPDIR}\plugins"
	File /r "D:\@Download\PCGen\PCGen_5713c\plugin\gmgen\plugins\*.*"
	
	SectionEnd
	
SubSectionEnd

Section "-Local" Section4

	; Set Section properties
	SetOverwrite ifnewer

	; Set Section Files and Shortcuts
	SetOutPath "$INSTDIR\${APPDIR}\Local\"
	File /r "D:\@Download\PCGen\Local\*.*"
	
	; Create Shortcuts
	SetOutPath "$INSTDIR\${APPDIR}\"
	CreateDirectory "$SMPROGRAMS\PCGen\${APPDIR}"
	CreateShortCut "$DESKTOP\${APPDIR}.lnk" "$INSTDIR\${APPDIR}\pcgen_high_mem.bat" "" "$INSTDIR\${APPDIR}\Local\PCGen2.ico"
	CreateShortCut "$SMPROGRAMS\PCGEN\${APPDIR}\${APPDIR}-High.lnk" "$INSTDIR\${APPDIR}\pcgen_high_mem.bat" "" "$INSTDIR\${APPDIR}\Local\PCGen2.ico"
	CreateShortCut "$SMPROGRAMS\PCGEN\${APPDIR}\${APPDIR}.lnk" "$INSTDIR\${APPDIR}\pcgen.bat" "" "$INSTDIR\${APPDIR}\Local\pcgen.ico"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\Release Notes.lnk" "$INSTDIR\${APPDIR}\pcgen-release-notes-5713.html" "" "$INSTDIR\${APPDIR}\Local\knight.ico"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\News.lnk" "http://pcgen.sourceforge.net/01_news.php" "" "$INSTDIR\${APPDIR}\Local\queen.ico"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\uninstall-${APPDIR}.lnk" "$INSTDIR\uninstall-${APPDIR}.exe"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\Manual.lnk" "$INSTDIR\${APPDIR}\docs\index.html" "" "$INSTDIR\${APPDIR}\Local\castle.ico"

SectionEnd

Section -FinishSection

	WriteRegStr HKLM "Software\${APPDIR}" "" "$INSTDIR\${APPDIR}"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}" "DisplayName" "${APPDIR}"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}" "UninstallString" "$INSTDIR\${APPDIR}\uninstall.exe"
	WriteUninstaller "$INSTDIR\uninstall-${APPDIR}.exe"

SectionEnd

; Modern install component descriptions
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	!insertmacro MUI_DESCRIPTION_TEXT ${Section1} "This is the PCGen Core"
	!insertmacro MUI_DESCRIPTION_TEXT ${Section2} "This section installs the data sets you need"
	!insertmacro MUI_DESCRIPTION_TEXT ${Section3} "This section installs the plug ins you may need"
	!insertmacro MUI_DESCRIPTION_TEXT ${Section4} "This is for icons and such"
!insertmacro MUI_FUNCTION_DESCRIPTION_END

Section Uninstall

	; Delete self
	Delete "$INSTDIR\uninstall-${APPDIR}.exe"

	; Remove from registry...
	DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPNAMEANDVERSION}"
	DeleteRegKey HKLM "Software\${APPNAME}\${APPDIR}"
	
	; Delete Desktop Shortcut
	Delete "$DESKTOP\${APPDIR}.lnk"
	
	MessageBox MB_YESNO|MB_ICONEXCLAMATION "Do you wish a full uninstall? This will  remove all versions of pcgen from your computer." IDYES Full IDNO Partial

	Full:
	; Delete Shortcut Directory
	RMDir /r "$SMPROGRAMS\PCGen"
	; Clean up PCGen program directory
	RMDir /r "$INSTDIR"
	Goto End
	
	Partial:
	; Delete Shortcut Directory
	RMDir /r "$SMPROGRAMS\PCGen\${APPDIR}"
	; Clean up PCGen program directory
	RMDir /r "$INSTDIR\${APPDIR}"
	
	End:
	
SectionEnd

; eof
; Script generated with the Venis Install Wizard

; Define constants
!define APPNAME "PCGen"
!define APPNAMEANDVERSION "PCGen 5.7.4"
!define APPDIR "PCGen574"
!define TargetVer "1.4"

; Main Install settings
Name "${APPNAMEANDVERSION}"
InstallDir "$PROGRAMFILES\${APPNAME}"
InstallDirRegKey HKLM "Software\${APPNAME}" ""
OutFile "C:\Documents and Settings\Administrator\Desktop\Setup-${APPDIR}.exe"
SetCompressor lzma
CRCCheck on

;	Look and style
ShowInstDetails show
BGGradient 000000 800000 FFFFFF
InstallColors FF8080 000030

; Modern interface settings
!include "MUI.nsh"

!define MUI_ABORTWARNING

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "D:\@Download\PCGen\PCGen_574\docs\acknowledgments\PCGen_License.txt"
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

Section "PCGen" Section1

	; Set Section properties
	SetOverwrite ifnewer

	; Set Section Files and Shortcuts
	SetOutPath "$INSTDIR\PCGen574\"
	File /r "D:\@Download\PCGen\PCGen_574\*.*"
	
	CreateDirectory "$SMPROGRAMS\PCGen"
	CreateShortCut "$DESKTOP\PCGen574.lnk" "$INSTDIR\PCGen574\pcgen.bat"
	CreateShortCut "$SMPROGRAMS\PCGen\PCGen574..lnk" "$INSTDIR\PCGen574\pcgen.bat"
	CreateShortCut "$SMPROGRAMS\PCGen\Manual.lnk" "$INSTDIR\PCGen574\pcgen-release-notes-574.html"
	CreateShortCut "$SMPROGRAMS\PCGen\News.lnk" "http://pcgen.sourceforge.net"
	CreateShortCut "$SMPROGRAMS\PCGen\PCGen574-Uninstall.lnk" "$INSTDIR\PCGen574\uninstall.exe"
	
SectionEnd

Section "-Local" Section2

	; Set Section properties
	SetOverwrite ifnewer

	; Set Section Files and Shortcuts
	SetOutPath "$INSTDIR\Local\"
	File /r "D:\@Download\PCGen\Local\*.*"

SectionEnd

Section "!Java" Section3
	
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
  ExecShell open "http://java.com/en/download/windows_xpi.jsp"
	Goto End

  FoundVM:
	DetailPrint "Java was found"
	DetailPrint $JREVer
	DetailPrint $JDKVer
	IntCmp $JREVer ${TargetVer} VMGood VMOld VMGood
	IntCmp $JDKVer ${TargetVer} VMGood VMOld VMGood
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
                    IDNO End
  ExecShell open "http://java.com/en/download/windows_xpi.jsp"
	Goto End
	
	Error:
	MessageBox MB_OK "Error during Java Detection"

	End:

SectionEnd

Section -FinishSection

	WriteRegStr HKLM "Software\${APPDIR}" "" "$INSTDIR"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}" "DisplayName" "${APPDIR}"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}" "UninstallString" "$INSTDIR\${APPDIR}\uninstall.exe"
	WriteUninstaller "$INSTDIR\${APPDIR}\uninstall.exe"

SectionEnd

; Modern install component descriptions
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	!insertmacro MUI_DESCRIPTION_TEXT ${Section1} "This will install PCGen Full"
	!insertmacro MUI_DESCRIPTION_TEXT ${Section2} "This section installs the localization files"
	!insertmacro MUI_DESCRIPTION_TEXT ${Section3} "Install Java Runtime Environment, needed to run PCGen."
!insertmacro MUI_FUNCTION_DESCRIPTION_END

;Uninstall section
Section Uninstall

	; Delete self
	Delete "$INSTDIR\${APPDIR}\uninstall.exe"

	;Remove from registry...
	DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPNAME}"
	DeleteRegKey HKLM "SOFTWARE\${APPNAME}"

	; Delete Shortcuts
	Delete "$DESKTOP\${APPDIR}.lnk"
	Delete "$SMPROGRAMS\PCGen\PCGen.lnk"
	Delete "$SMPROGRAMS\PCGen\Manual.lnk"
	Delete "$SMPROGRAMS\PCGen\News.lnk"


	; Delete Shortcut Directory
	RMDir "$SMPROGRAMS\${APPNAME}"

	; Clean up PCGen program directory
	RMDir "$INSTDIR\${APPDIR}"
	
	; Leave Local Files Intact
	
SectionEnd

; eof
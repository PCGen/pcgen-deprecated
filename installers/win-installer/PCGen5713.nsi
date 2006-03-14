; Script generated with the Venis Install Wizard

; Define constants
!define APPNAME "PCGen"
!define APPNAMEANDVERSION "PCGen 5.7.13"
!define APPDIR "PCGen5713"
!define TargetVer "1.4"
!define OverVer "1.5"
!define OutName "setup_simp_pcgen5713_win"

; Main Install settings
Name "${APPNAMEANDVERSION}"
InstallDir "$PROGRAMFILES\${APPNAME}\${APPDIR}"
InstallDirRegKey HKLM "Software\${APPNAME}" ""
OutFile "C:\Documents and Settings\Lisa\Desktop\${OutName}.exe"
SetCompressor lzma
CRCCheck on

;	Look and style
ShowInstDetails show
InstallColors FF8080 000030

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

	; Set Section properties
	SetOverwrite ifnewer

	; Set Section Files and Shortcuts
	SetOutPath "$INSTDIR\"
	File /r "D:\@Download\PCGen\pcgen5713_full\pcgen5713\*.*"
	
	CreateDirectory "$SMPROGRAMS\PCGen\${APPDIR}"
	CreateShortCut "$DESKTOP\${APPDIR}.lnk" "$INSTDIR\pcgen.bat"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\${APPDIR}-High.lnk" "$INSTDIR\pcgen_high_mem.bat"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\${APPDIR}.lnk" "$INSTDIR\pcgen.bat"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\Release Notes.lnk" "$INSTDIR\pcgen-release-notes-5713.html"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\News.lnk" "http://pcgen.sourceforge.net/01_news.php"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\uninstall-${APPNAME}.lnk" "$INSTDIR\uninstall.exe"
	CreateShortCut "$SMPROGRAMS\PCGen\${APPDIR}\Manual.lnk" "$INSTDIR\docs\index.html"
	
SectionEnd

Section "-Local" Section2

	; Set Section properties
	SetOverwrite ifnewer

	; Set Section Files and Shortcuts
	SetOutPath "$INSTDIR\Local\"
	File /r "D:\@Download\PCGen\Local\*.*"

SectionEnd

Section -FinishSection

	WriteRegStr HKLM "Software\${APPDIR}" "" "$INSTDIR"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}" "DisplayName" "${APPDIR}"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}" "UninstallString" "$INSTDIR\uninstall.exe"
	WriteUninstaller "$INSTDIR\uninstall.exe"

SectionEnd

; Modern install component descriptions
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	!insertmacro MUI_DESCRIPTION_TEXT ${Section1} "This will install PCGen Full"
	!insertmacro MUI_DESCRIPTION_TEXT ${Section2} "This section installs the localization files"
!insertmacro MUI_FUNCTION_DESCRIPTION_END

;Uninstall section
Section Uninstall

	; Delete self
	Delete "$INSTDIR\PCGen\${APPDIR}\uninstall.exe"

	;Remove from registry...
	DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APPDIR}"
	DeleteRegKey HKLM "SOFTWARE\${APPNAME}"

	; Delete Shortcuts
	Delete "$DESKTOP\${APPDIR}.lnk"
	Delete "$SMPROGRAMS\PCGen\${APPDIR}.lnk"
	Delete "$SMPROGRAMS\PCGen\Manual.lnk"
	Delete "$SMPROGRAMS\PCGen\News.lnk"
	Delete "$SMPROGRAMS\PCGen\PCGen574-Uninstall.lnk"


	; Delete Shortcut Directory
	RMDir /r "$SMPROGRAMS\PCGen\${APPDIR}"

	; Clean up PCGen program directory
	RMDir /r "$INSTDIR"
	
	; Leave Local Files Intact
	
SectionEnd

; eof
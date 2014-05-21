"=============================================================================
" File:		pcgen.vim
" Author:	Luc Hermitte <EMAIL:hermitte at free.fr>
" URL:	http://hermitte.free.fr/vim/ressources/vimfiles/syntax/pcgen.vim
" Version:	0.5
" Created:	17th jun 2002
" Last Update:	25th oct 2002
" 20th May 2014 DG added various tokens and file definitions
"------------------------------------------------------------------------
" Description:	VIM syntax & ftplugin file for PCGen <http://pcgen.sf.net/> 
" 		lst files
" Features: {{{
"	(*) Displays all the tags in several colors :
"	    PCGen syntaxic elements | VIM syntaxic groups
"		Comments     		<=> Comments
"	    	tags         		<=> Keyword
"	    	not recognized tags	<=> Error
"	    	CLASS name   		<=> Type
"	    	class levels 		<=> Constant
"	    	BONUS type   		<=> Type
"	    	variables		<=> Identifier
"	    	PREREQuisites		<=> WarningMsg
"	(*) Defines appropriated settings (like a horizontal scrollbar)
"	(*) Remaps <tab> & <s-tab> to jump from one pcgen entry to the other
" }}}
"------------------------------------------------------------------------
" Installation:	 {{{
"	0- To be used with the text editor VIM 6.0+ (<http://vim.sf.net/>)
"	1- Drop it into your one of your $/syntax/ folder
"	2- Add into your myfiletypes.vim file : 
"		au BufNewFile,BufRead *.lst,*.pcc set ft=pcgen
"	NB: this will override the default assocition to assembler source files.
"	3- You may also have to change the file $VIM/filetype.vim l.1365
"	   "if !did_filetype()" must become : "if '' == &ft"
"	   Or, if your vim version (:version) is superior than 6.1.200, add
"		let g:ft_ignore_pat = 'lst'
"	   into your .vimrc.
"	   
" }}}
" History:	{{{
" 	Version 0.1: {{{
" 	    (*) Syntax highlighting scheme for pcgen 2.6.8
" 	    (*) Some settings
" 	    }}}
" 	Version 0.2: {{{
" 	    (*) Mappings to do autocompletion {{{
" 		CTRL-X CTRL-T  and CTRL-SPACE	(thesaurus overidedà
" 			CTRL-T	: expands the TAG
" 			CTRL-N	: expands next     TAG in the list
" 			CTRL-P	: expands previous TAG in the list
" 			CTRL-l	: expands a TAG with a popmenu
" 		CTRL-X CTRL-l	: expands a TAG with a popmenu
" 		CTRL-X CTRL-D	: expands DEFINEd variables
" 		}}}
" 	    (*) Mappings to navigate among tags {{{
" 		n_<TAB> and n_<S-TAB> : from one tab to another
" 		i_<TAB> and i_<S-TAB> : the same, but in insert mode.
" 		}}}
" 	    }}}
" 	Version 0.3: {{{
" 	    (*) Syntax highlighting scheme updated for pcgen 4.0.0
" 	    }}}
" 	Version 0.4: {{{
" 	    (*) Syntax highlighting scheme updated for pcgen 4.1.2
" 	    }}}
" 	Version 0.5: {{{
" 	    (*) Syntax highlighting scheme updated for pcgen 4.1.3
" 	    }}}
" 	Version 0.6: {{{
" 	    (*) added tokens and file types for 6.2.1, including gamemode files
" 	    }}}
" }}}
" TODO:		{{{
"	(*) handle formulas (by the way of an option ... ?)
"	(*) highlight variables in CHOOSE 
"	(*) Remove unused stuff
"	(*) Update the tags from PCgen 2.6.8 to 2.7.x
"	(*) Continue to extend the i_CTRL-X_CTRL-K i_CTRL-X_CTRL-N facilities to
"	    insert 2nd level tags according to PCGen syntax ; 2nd level tags :
"	    the parameters of BONUS and CHOOSE
"	(*) Extend the AddTagsxxx commands in order to specify the authorized
"	    arguments for the tag
"	(*) Fix the GoZsh issue : if not forced, vim will split-open a new
"	    buffer containing the results of the search, and set the filetype
"	    to 'conf'
"	(*) ^X^T may leave two ':' when expanding stuff like 'SP|x:' ('|' beeing
"	    the cursor position) ; Fix it!
"	(*) Distinguish the tags, that must be of the first column of lines,
"	    with the ':AddTagsxxx' commands
"	(*) Better support of cygwin bash.
" }}}
"=============================================================================
"
"=============================================================================
" PCGen syntax {{{
" ' something here sets buftype and prevents writing pcc files...
"------------------------------------------------------------------------
"| s:StringPresentInfile(StringToFind, FileName)        | {{{
"| return:  1 if string exists in file                  |
"|          0 is string isn't found                     |
"|         -1 error (such as file not readable)         |
"+------------------------------------------------------+
function! s:StringPresentInfile(StringToFind, FileName)
  if  !filereadable(a:FileName) | return '' | endif

  if &shell =~ 'sh'
    let v=&verbose	" verbose will otherwise mix up with redir
    set verbose=0
    redir @a
    execute "grep '".a:StringToFind."' ".a:FileName
    redir END
    if v>0 | echo "execute grep '".a:StringToFind."' ".a:FileName."\n" | endif
    let &verbose=v
    let ii = escape(':!'.&grepprg." '".a:StringToFind."' ".a:FileName, '\')
    let aa = substitute(@a, ii.".\\{-}\n", '', '')
    return (aa =~ a:StringToFind) ? aa : ''
  else  " Win32 :-(
    silent execute "normal :0split\<CR>:e ".a:FileName."\<CR>"
 	"DG - try and fix the buftype issue on .pcc files, disable the next line
    "setlocal buftype=nofile
    setlocal bufhidden=hide
    setlocal noswapfile
    "   This last local setting seems to be enough
    setlocal nobuflisted
    " open a first line in case the string is the file's first line
    let s = search(a:StringToFind)
    let RC = (s != 0) ? getline(s) : ''
    silent quit
    return RC
  endif

  " old way {{{
  if !has("win32") || (&shell =~ 'sh')  " UNIX :-)
    "silent execute "!grep -q \"".a:StringToFind."\" ".a:FileName
    silent execute "!grep  '".a:StringToFind."' ".a:FileName
    "grep exit status is 0 if it finds the word "
    let RC = (v:shell_error == 0) ? 1 : 0
  endif " }}}
endfunction " }}}
"------------------------------------------------------------------------
" Prepare the syntax scheme {{{
" For version 5.x: Clear all syntax items
" For version 6.x: Quit when a syntax file was already loaded
if version < 600
  " syntax clear
  call confirm('VIM version required must be > 6.00', 'ok', 1, 'Error')
  finish " ie provoque an error
elseif exists("b:current_syntax")
  finish
endif
" }}}
"------------------------------------------------------------------------
" Commands for managing the Syntax {{{
"
" Function: s:Add1KeywordToVarAndSyn() {{{
function! s:Add1KeywordToVarAndSyn(syn_group, var, key, special)
  " here, s:{a:var} must exists
  " exe 'let s:'.a:var.' = a:key . " ". s:'.a:var
  let s:{a:var} = a:key . " " . s:{a:var}
  if a:key =~ '[^[:alpha:]]'
    " used for 'CONTA[I]NS'
    exe 'syn match   '.a:syn_group.' contained '.a:special.a:key
  else
    exe 'syn keyword '.a:syn_group.' contained '.a:special.a:key
  endif
endfunction
" }}}

" Function: s:AddKeywordsToVarAndSyn() {{{
function! s:AddKeywordsToVarAndSyn(syn_group, var, ...)
  if !exists('s:'.a:var)   | exe 'let s:'.a:var." = ''" 
  endif
  let e = a:0 | let i = 1
  let special = ''
  while i <= e
    if a:{i} =~ 'nextgroup\|contains\|oneline'
      let special = a:{i} . ' ' . special
    else
      call s:Add1KeywordToVarAndSyn(a:syn_group, a:var, a:{i}, special)
    endif
    let i = i + 1
  endwhile
endfunction
" }}}

command! -buffer -nargs=+ AddTag1st 
      \ :call <sid>AddKeywordsToVarAndSyn('pcgenTag1stLvl', 
      \			b:pcgenFileType, <f-args>)

command! -buffer -nargs=+ AddTag2ndADDAction
      \ :call <sid>AddKeywordsToVarAndSyn('pcgenTag2ndADDAction',
      \			'ADDAction', <f-args>)
command! -buffer -nargs=+ AddTag2ndBONUSAction
      \ :call <sid>AddKeywordsToVarAndSyn('pcgenTag2ndBONUSAction', 
      \			'BONUSAction', 'nextgroup=pcgenVarID', <f-args>)

function! s:EchoVar(var)
  echo s:{a:var}
endfunction
command! -buffer -nargs=1 EchoVar :call <sid>EchoVar("<args>")

" }}}
"------------------------------------------------------------------------
" Recognize the exact filetype {{{
" if     exists(':GoBash') | GoBash 
" elseif exists(':GoZsh')  | GoZsh 
" endif

" DG added ABILITY and BIOSET - every File type must be defined here, with the
" tag from the PCC that identifies it.
" hmmm, this detects ABILITYCATEGORY as ABILITY ?
let s:pcgenFTs = 'RACE\|ABILITYCATEGORY\|ABILITY\|BIOSET\|CLASS\|SKILL\|FEAT\|DOMAIN\|DEITY\|SPELL\|'.
      \ 'WEAPONPROF\|ARMORPROF\|SHIELDPROF\|LANGUAGE\|CLASSSKILL\|CLASSSPELL\|'. 
      \ 'TEMPLATE\|EQUIPMOD\|EQUIPMENT\|COINS\|KIT\|MISC\|COMPANIONMOD'

" lookup the filename in the current .pcc file, and determine its type
let b:pcgenFileType = matchstr(
      \ s:StringPresentInfile(expand('%:t'), glob('*.pcc')), 
      \ '\('.s:pcgenFTs.'\)\ze:')
if '' == b:pcgenFileType
  let b:filename = expand('%:t')
" DG - add a default 'MISC' setting for gamemode files
  let b:pcgenFileType = 'MISC'
  if b:filename =~ '\.pcc$'
    let b:pcgenFileType = 'CAMPAIGN'
  elseif b:filename =~? 'CLASSSPELL\|CLASSPOWER'
    let b:pcgenFileType = 'CLASSSPELL'
  elseif b:filename =~? 'SPELL\|POWER'
    let b:pcgenFileType = 'SPELL'
  elseif b:filename =~? 'CLASSSKILL'
    let b:pcgenFileType = 'CLASSSKILL'
  elseif b:filename =~? 'CLASS'
    let b:pcgenFileType = 'CLASS'
  elseif b:filename !~? 'EQUIPMOD' && b:filename =~? 'EQUIP'
    let b:pcgenFileType = 'EQUIPMENT'
  elseif b:filename =~? 'weapprof,'
    let b:pcgenFileType = 'WEAPONPROF'
  elseif b:filename =~? 'kit'
    let b:pcgenFileType = 'KIT'
  elseif b:filename =~? 'ABILITYCATEGORY'
    let b:pcgenFileType = 'ABILITYCATEGORY'
  elseif b:filename =~? 'ABILITY'
    let b:pcgenFileType = 'ABILITY'
  elseif b:filename =~? 'COMPANIONMOD'
    let b:pcgenFileType = 'COMPANIONMOD'
  else
    let b:pcgenFileType = toupper(
	  \ matchstr(b:filename, '\c\('.s:pcgenFTs.'\)'))
	 if 0 == strlen(b:pcgenFileType)
	 let b:pcgenFileType = 'MISC'
	  " let b:pcgenFileType = confirm(
		" \ 'Exact PCGen filetype not recognized. Pick one:'
		" \ substitute(s:pcgenFTs, '\\|', '&\n', 'g'), 1)
	endif
  endif
endif
" }}}
"------------------------------------------------------------------------
" Definitions for PCGen {{{
" == Unmatched tags ==
" syn region pcgenUnreconReg start="^" start="\t"ms=s+1 end=":"me=e-1 contains=pcgenUnreconTag
syn match pcgenTag		/^\|\t/ nextgroup=pcgenUnreconTag,@pcgenTags
syn match pcgenUnreconTag	contained /[^[:tab:]]\{-}:/ contains=NONE
syn match pcgenItem		/^[^[:tab:]:]*\t/me=e-1

" == Comments and tools ==
syn keyword pcgenTodo		TODO todo FIXME fixme TBD tbd	contained
syn match   pcgenComment	"#.*$"	contains=pcgenTodo

" == Ids ==
syn match pcgenID		contained /[^[:tab:]]/
syn match pcgenName		contained /[^[:tab:]]/

" == Reserved Tags ==
syn cluster pcgenTags	contains=pcgenSources,pcgenClassStuff,pcgenTag1stLvl,pcgenPrereq,pcgenGeneralStuff
" Sources
syn keyword pcgenSources	
      \ SOURCE SOURCEPAGE SOURCEWEB SOURCELONG SOURCESHORT SOURCEDATE
"
" == PreReqs Tags == 
syn match   pcgenPrereq	/!\=PRE\k*/

" Global Tags
AddTag1st CSKILL CCSKILL DR LANGAUTO SA SPELL SR
      \  UDAM UMULT VISION WEAPONAUTO 
      \  OUTPUTNAME

" == File Specific Tags == 
if     b:pcgenFileType == 'CAMPAIGN'   " Campaign *.pcc {{{
  AddTag1st CAMPAIGN GAME TYPE RANK
	\ LSTEXCLUDE PCC INFOTEXT
	\ ISOGL FORWARDREF GAMEMODE BOOKTYPE SHOWINMENU
  " AddTag1st ISD20 
  " lst file tags
  AddTag1st CLASS CLASSSKILL CLASSSPELL COMPANIONMOD COPYRIGHT
	\ DEITY DOMAIN EQUIPMENT EQUIPMOD FEAT 
  AddTag1st
	\ KIT LANGUAGE RACE SKILL TEMPLATE WEAPONPROF 
  " PRECAMPAIGN still shows as error?
  	\ PRECAMPAIGN ABILITY ABILITYCATEGORY
  " Depreciated: COINS INCLUDE EXCLUDE NFO REQSKILL 
  " }}}
elseif b:pcgenFileType == 'ABILITYCATEGORY'       " AbilityCategory.lst {{{
  " DG trying to add Ability file definition
  AddTag1st ABILITYCATEGORY EDITPOOL EDITABLE DISPLAYLOCATION CATEGORY ABILITY ABILITYLIST FRACTIONALPOOL PLURAL DESC TYPE VISIBLE KEY ASPECT 
	" \ REP 
  " + ADD, BONUS, CHOOSE, DEFINE
  " }}}
elseif b:pcgenFileType == 'ABILITY'       " Ability.lst {{{
  " DG trying to add Ability file definition
  AddTag1st CATEGORY ABILITY DESC TYPE VISIBLE KEY ASPECT SOURCEPAGE SOURCESHORT 
  	\ SOURCELONG DEFINE
  AddTag1st SPELLS PREMULT PREABILITY TEMPLATE SERVESAS
  	\ AUTO BENEFIT SORTKEY
  " these tags are still shown red...
  AddTag1st PREMULT PREVAREQ PREABILITY
	" \ REP 
  " + ADD, BONUS, CHOOSE, DEFINE
  " }}}
elseif b:pcgenFileType == 'BIOSET'       " Biosettings.lst {{{
  " DG trying to add biosettings file definition
  AddTag1st AGESET RACENAME BASEAGE MAXAGE AGEDIEROLL SEX CLASS
	" \ REP 
  " + ADD, BONUS, CHOOSE, DEFINE
  " }}}
elseif b:pcgenFileType == 'CLASS'      " Class.lst {{{
  syn match   pcgenClsLvl		/^\d\+/
  syn keyword pcgenClassStuff	CLASS nextgroup=pcgenDotName
  syn region  pcgenDotName 	contained start=":"  end="$" end="\t"me=e-1 contains=pcgenName oneline
  " Required tags
  AddTag1st ABB BAB CLASS HD 
	\ FORTITUDECHECK REFLEXCHECK WILLPOWERCHECK
  AddTag1st MAXLEVEL STARTSKILLPTS TYPE XTRAFEATS
  	\ SUBCLASSLEVEL COST ABILITY KIT PREALIGN
  AddTag1st TEMPLATE
  " Spellcasters tags
  AddTag1st ADDDOMAIN CAST DEITY DOMAIN
	\  ITEMCREATE  PROHIBITSPELL
  AddTag1st  KNOWN KNOWNSPELLS SPECIALTYKNOWN MEMORIZE PROHIBITED
	\  SPELLLIST SPELLSTAT SPELLTYPE SUBCLASS 
  AddTag1st  PROHIBITCOST CHOICE SPELLLEVEL
  " Other optional tags
  AddTag1st ATTACKCYCLE EXCLASS EXCHANGELEVEL
	\  FEATAUTO LANGBONUS MULTIPREEQS
	\ SKILLLIST 
  AddTag1st SPECIALS TEMPLATE  VFEAT 
	\  WEAPONBONUS CLASSTYPE
  AddTag1st PRESKILL PREPCLVL PRELANG PRERACE
  " + ADD, BONUS, DEFINE
  " Monster Specific Tags
  AddTag1st 
	\ MODTOSKILLS LEVELSPERFEAT PRERACETYPE

  " Depreciated Tags
	" \ GOLD SUBSA UATT AGESET CASTAS 

  " Unidentified tags:
  AddTag1st
	\ NAMEISPI VISIBLE
  " For starwars
  AddTag1st REP
	" \ DEF
  " }}}
" CLASSSPELL? deprecated file type?
elseif b:pcgenFileType == 'CLASSSPELL' " ClassSpell.lst {{{
  " AddTag1st CLASS
  syn keyword pcgenClassStuff	CLASS nextgroup=pcgenDotName
  syn region  pcgenDotName 	contained start=":"  end="$" end="\t"me=e-1 contains=pcgenName oneline
  " }}}
elseif b:pcgenFileType == 'DEITY'      " Deity.lst {{{
  AddTag1st ALIGN DEITYWEAP DESC DOMAINS 
	\ FOLLOWERALIGN NAMEISPI PANTHEON SYMBOL TYPE 
	" \ RACE
  " + BONUS, SA
  " }}}
elseif b:pcgenFileType == 'DOMAIN'     " Domain.lst {{{
  AddTag1st DESC FEAT 
	" \ RACE SKILL 
  " + BONUS, CHOOSE, DEFINE
  " }}}
elseif b:pcgenFileType == 'COMPANIONMOD'    " COMPANIONMOD.lst {{{
  AddTag1st FOLLOWER TYPE HD MASTERBONUSRACE ABILITY
  	\ KIT VFEAT AUTO DEFINESTAT
  AddTag1st COPYMASTERHP COPYMASTERBAB COPYMASTERCHECK
  " }}}
elseif b:pcgenFileType == 'EQUIPMENT'  " Equipment.lst {{{
  AddTag1st ACCHECK ALTCRITICAL ALTDAMAGE 
	\ ALTTYPE BASEITEM BASEQTY COST CRITMULT CRITRANGE DAMAGE EQMOD
  AddTag1st HANDS MAXDEX MOVE MODS NAMEISPI PROFICIENCY RANGE SIZE
	\ SPELLFAILURE SPROP TYPE VFEAT WT
  AddTag1st SLOTS WIELD RATEOFFIRE KEY ABILITY SPELLS
  	\ AUTO ALTCRITMULT ALTEQMOD QUALITY
  AddTag1st SORTKEY NUMPAGES PAGEUSAGE
  " DG added SLOTS WIELD and RATEOFFIRE above
  " + BONUS, DEFINE
  " special traitment for contains
  AddTag1st /\<CONTA[I]NS\>/
  " Depreciated: LONGNAME REACH 
  " }}}
elseif b:pcgenFileType == 'EQUIPMOD'   " Equipmod.lst {{{
  AddTag1st ARMORTYPE ASSIGNTOALL CHARGES COST COSTPRE
	\ IGNORES ITYPE KEY NAMEOPT PLUS REPLACES
	\ SPROP TYPE VISIBLE FORMATCAT SPELLS 
  "+ BONUS, DEFINE, CHOOSE, 
  " Depreciated: ADDPROF
  " }}}
elseif b:pcgenFileType == 'FEAT'       " Feat.lst {{{
  AddTag1st ADDSPELLLEVEL COST DESC MULT NAMEISPI STACK TYPE VISIBLE
  	\ COMPANIONLIST AUTO TEMPLATE
	" \ REP 
  " + ADD, BONUS, CHOOSE, DEFINE
  " }}}
elseif b:pcgenFileType == 'KIT'        " Kit.lst {{{
  " Header tags
  AddTag1st STARTPACK
  " Line tags
  AddTag1st COUNT FEAT GEAR PROF QTY RACIAL RANK REGION SKILL SPELLS
  	\ VISIBLE LANGBONUS EQUIPBUY EQMOD LOCATION SIZE MAXCOST
  AddTag1st 
	\ PRECLASS PRERACE PRELEVELMAX NAME RACE ALIGN
	\ CLASS STAT TYPE LEVEL FREE APPLY OPTION KIT SELECT
  AddTag1st TABLE VALUES LOOKUP SORTKEY
  "+ BONUS, DEFINE
  " }}}
elseif b:pcgenFileType == 'LANGUAGE'    " Language.lst {{{
  AddTag1st TYPE 
  " }}}
elseif b:pcgenFileType == 'RACE'       " Race.lst {{{
  AddTag1st AGE CR 
	\ FAVCLASS FEAT HANDS HEIGHT HITDICE HITDICEADVANCEMENT 
  AddTag1st LANGBONUS LEVELADJUSTMENT
  AddTag1st MFEAT MONSTERCLASS MOVE NATURALATTACKS
	\ REACH SAVES SIZE SKILLMULT AUTO
  AddTag1st STARTFEATS TEMPLATE TYPE VFEAT WEAPONBONUS WEIGHT 
	\ XTRASKILLPTSPERLVL
  AddTag1st PREABILITY PREVAREQ PRESIZEEQ SPELLLEVEL
  	\ PRERACE COST SPELLS
  AddTag1st UNENCUMBEREDMOVE FACE DEFINESTAT RACETYPE
	\ RACESUBTYPE ABILITY LEGS KEY MONCSKILL KIT
  " + BONUS, DEFINE, CHOOSE
  " syn match pcgenRaceStuff contained /STATADJ\S\{-}/
  AddTag1st /STATADJ\S\{-}\ze:/
  " Depreciated: RACENAME BAB 
  " }}}
elseif b:pcgenFileType == 'SKILL'      " Skill.lst {{{
  AddTag1st ACHECK CLASSES EXCLUSIVE KEYSTAT REQ SYNERGY TYPE
	\ USEUNTRAINED SERVESAS VISIBLE TEMPDESC KEY SITUATION
  AddTag1st TEMPBONUS AUTO
  " \ SAVEINFO ROOT 
  " + BONUS, CHOOSE, DEFINE, 
  " }}}
elseif b:pcgenFileType == 'SPELL'      " Spell.lst {{{
  AddTag1st CASTTIME CLASSES COMPS COST CT DESCRIPTOR DURATION 
	\ EFFECTS EFFECTTYPE ITEM NAMEISPI RANGE SAVEINFO SCHOOL 
  	\ TARGETAREA DESC
  AddTag1st SPELLRES STAT SUBSCHOOL TYPE VARIANTS XPCOST 
	\ DOMAINS TEMPDESC
  " }}}
elseif b:pcgenFileType == 'TEMPLATE'   " Template.lst {{{
  AddTag1st BONUSFEATS GENDERLOCK HD HITDICESIZE
	\ CR KIT LANGBONUS
	\ LEVEL LEVELADJUSTMENT
  AddTag1st 
	\ HEIGHT WEIGHT SIZE
	\ FEAT FAVOREDCLASS GOLD
  AddTag1st 
	\ MOVE MOVEA MOVECLONE REGION SUBREGION REMOVABLE SPELL 
	\ SUBRACE TEMPLATE TYPE VISIBLE WEAPONBONUS 
  AddTag1st 
  	\ KEY DEFINESTAT AUTO HITDIE RACETYPE RACESUBTYPE
  	\ TEMPDESC ABILITY NATURALATTACKS SPELLS
  AddTag1st 
  	\ TEMPBONUS FACE REACH LEGS HANDS
  	\ PREVARGTEQ PRERACE PRECLASS PRESIZEEQ PREMOVE PRETEMPLATE
  " + BONUS, DEFINE, CHOOSE
  " Depreciated: AGE BONUSSKILLPOINTS LEVELSPERFEAT NONPP NATURALARMOR 
  " \ HITDICE POPUPALERT
  " \ STR DEX CON INT WIS CHA 
  " }}}
elseif b:pcgenFileType == 'WEAPONPROF' " WeaponProf.lst {{{
  AddTag1st HANDS SIZE TYPE
  "+ BONUS, DEFINE
  " }}}
elseif b:pcgenFileType == 'ARMORPROF' " ArmorProf.lst {{{
  AddTag1st SIZE TYPE
  "+ BONUS, DEFINE
  " }}}
elseif b:pcgenFileType == 'SHIELDPROF' " ShieldProf.lst {{{
  AddTag1st HANDS SIZE TYPE
  "+ BONUS, DEFINE
  " }}}
elseif b:pcgenFileType == 'MISC' " gamemode/unknown files {{{
  AddTag1st GAME
  "+ BONUS, DEFINE
  " }}}
endif
"
" Common Stuff {{{
"  -- DEFINE
syn keyword pcgenGeneralStuff	contained DEFINE nextgroup=pcgenDefine
syn region  pcgenDefine		contained start=":"  end="|"me=e-1 contains=pcgenID oneline

" -- BONUS
syn keyword pcgenGeneralStuff	contained BONUS  nextgroup=pcgenBonus
syn region  pcgenBonus		contained start=":"  end="$\|\t"me=e-1 contains=pcgenTag2ndBONUSAction,pcgenUnreconBonusAc oneline
" syn match   pcgenUnreconBonusAc	contained /[^|[:tab:]]\{-}|/ contains=NONE
" 
AddTag2ndBONUSAction
      \ CHECKS CLASS 
      \ DAMAGE DOMAIN ESIZE SIZEMOD HD LANGUAGES MISC MOVE PCLEVEL 
AddTag2ndBONUSAction
      \ RANGEADD RANGEMULT SKILLRANK SKILLMAXRANK SKILLPOINTS
      \ SPELLCAST SPELLKNOWN SPELLCASTMULT SPELL STAT TOHIT
AddTag2ndBONUSAction
      \ WEAPON WEAPONPROF
      \ VAR COMBAT SKILL EQMWEAPON 
syn region  pcgenVarID		contained start="|" end="|"me=e-1 contains=pcgenID oneline

"  -- ADD
syn keyword pcgenGeneralStuff	contained ADD  nextgroup=pcgenADD
syn region  pcgenADD		contained start=":"  end="$\|\t"me=e-1 contains=pcgenTag2ndADDAction oneline
AddTag2ndADDAction
      \ SPECIAL FEAT label CLASSSKILLS LIST 
      \ SPELLCASTER VALUE WEAPONBONUS FORCEPOINT 
      \ WEAPONPROFS FAVOREDCLASS SPECIAL 
      \ INIT LEVEL

"  -- CHOOSE
syn keyword pcgenGeneralStuff	contained CHOOSE
" }}}

" }}}
"------------------------------------------------------------------------
" Define the default highlighting. {{{
" For version 5.7 and earlier: only when not done already
" For version 5.8 and later: only when an item doesn't have highlighting yet
if version >= 508 || !exists("did_pcgen_syntax_inits")
  " if version < 508
	" let did_pcgen_syntax_inits = 1
	" command -nargs=+ HiLink hi link <args>
  " else
    command -nargs=+ HiLink hi def link <args>
  " endif

  HiLink	pcgenUnreconTag		Error
  HiLink	pcgenTags		Keyword
  HiLink	pcgenTag1stLvl		Keyword
  HiLink	pcgenSources		Keyword
  HiLink	pcgenStatemen		Statement
  HiLink	pcgenClassStuff		Keyword
  HiLink	pcgenGeneralStuff	Keyword
  HiLink	pcgenFormulaTag		Keyword
  HiLink	pcgenClsLvl		Constant
  " HiLink	pcgenClass		Identifier
  HiLink	pcgenItem		Underlined
  HiLink	pcgenID			Identifier
  HiLink	pcgenName		Type
  HiLink	pcgenTag2ndBONUSAction	Type
  HiLink	pcgenTag2ndADDAction	Type
  HiLink	pcgenUnreconBonusAc	Error

  HiLink	pcgenPrereq		WarningMsg

  " HiLink	pcgenConstant		Constant
  " HiLink	pcgenNumber		Number
  " HiLink	pcgenString		String
  " HiLink	pcgenBoolean		Boolean
  " HiLink	pcgenDelimiter		Delimiter
  " HiLink	pcgenOverload		Special

  HiLink	pcgenTodo		Todo
  HiLink	pcgenComment		Comment

  delcommand HiLink
endif " }}}
"------------------------------------------------------------------------
let b:current_syntax = "pcgen"
" }}}
"=============================================================================
" Definitions {{{
if has("win32") " {{{
  "DG altered font
  "set guifont=Andale_Mono:h8:cANSI
  set guifont=Fixedsys:h8:cANSI
endif
" }}}
" Settings {{{
setlocal tw=10000
setlocal nowrap
setlocal guioptions+=b		" bottom scrollbar
setlocal define=DEFINE:
setlocal complete+=d

if !exists('maplocalleader')
  let maplocalleader = ","
endif

" Settings }}}
" Mappings & Menus {{{
" -- Completion {{{

" Function: s:PrevKeyword() {{{
function! s:PrevKeyword()
  let c  = col('.') - 1
  let ll = getline('.')
  let ll1 = strpart(ll,0,c)
  let ll1 = matchstr(ll1,'\_s\=\k*$')
  return ll1
endfunction
" }}}

" Function: s:ChangeWord(word) {{{
function! s:ChangeWord(word)
  if getline('.')[col('.')-1] == ':'
    :exe "normal! xhviwc".a:word.":\el"
  else
    :exe "normal! hviwc".a:word.":\el"
  endif
  if strpart(getline('.'), col('.')-2, 2) == '::'
    " call confirm(strpart(getline('.'),col('.')-2,2), 'ok')
    normal x
  " else
    " call confirm(strpart(getline('.'),0,col('.')).'#', 'ok')
  endif
endfunction
" }}}

" Function: s:GetAlternatives(tag) {{{
function! s:GetAlternatives(tag)
  exe 'let r=substitute(s:'.b:pcgenFileType.', "\\<\\(".a:tag."\\)\\@!\\k\\{-}\\>", "", "g" )'
  let r = substitute(r, '\s\+', ' ', 'g')
  let s = r
  let s:nb_tags = 0
  while s !~ '^\s*$'
    let w = matchstr(s, '\<\k\+\>', 1)
    let s = strpart (s, strlen(w)+1)
    if has('gui_running') && has('menu')
      exe 'amenu ]Possible\ Choices.'.
	    \ substitute(w,'^.\{'.strlen(a:tag).'}', '\0\&', '').
	    \ ' :call <sid>ChangeWord("'.w.'")<cr>'
    endif
    let s:nb_tags = s:nb_tags + 1
  endwhile
  return r
endfunction
" }}}

" Function: s:Context() {{{
function! s:Context()
  let prev = s:PrevKeyword()
  let len  = strlen(prev)
  if 0 == len			" Nothing found
    return 0
  elseif prev[0] == "\t"	" A tag
    let s:res = strpart(prev,1)
    " call confirm('-'.prev.'-', 'ok')
    return 1
  elseif len == col('.') - 1	" The tag starting the line
    let s:res = prev
    return 1
  else
    let s:res = prev
    return 2
  endif
endfunction
" }}}

" Function: s:ShowList() {{{
function! s:ShowList()
  if has('gui_running') && has('menu') && 1 == s:Context()
    if !exists('s:alts') || !strlen(s:alts)
      let s:alts = s:GetAlternatives(s:res)
      let s:cur = 1
    endif
    if s:alts =~ '^\s*$' | return '' | endif
    popup ]Possible\ Choices
  endif
  return ''
endfunction
" }}}

" Function: s:NextWordFromList(dir) {{{
function! s:NextWordFromList(dir)
  if a:dir > 0
    let alt = matchstr(s:alts, '\<\k\+\>', s:cur)
    " call confirm('<'.alt.'> from :'.s:alts, 'ok')
    let s:cur = s:cur + strlen(alt) + 1
    if s:cur == strlen(s:alts) | let s:cur = 1 | endif
  else
    if s:cur == 1 | let s:cur = strlen(s:alts) | endif
    let alt = matchstr(strpart(s:alts,0,s:cur-1), '\<\k\+\>$')
    let s:cur = s:cur - strlen(alt) - 1
    let cur = (s:cur == 1) ? strlen(s:alts) : s:cur
    let alt = matchstr(strpart(s:alts,0,cur-1), '\<\k\+\>$')
  endif
  return alt
endfunction
" }}}

" Function: s:GetAlt(dir) {{{
" Inspired from Gergely Kontra's ComplMenu Script.
function! s:GetAlt(dir)
  if 1 == s:Context() 
    let s:alts = s:GetAlternatives(s:res)
    let s:cur = 1
    if s:alts =~ '^\s*$' | return '' | endif
    let dir = a:dir
    while 1
      " exe "normal! i:\<esc>x"
      let alt = s:NextWordFromList(dir)
      " call s:ChangeWord(alt)
      exe "normal! hviwc".alt.":\<esc>"
      redraw!
      echohl StatusLineNC
      echo "\r-- PCGen's tags completion (/^T/l/^N/^P/n/p)  "
	    \ .s:nb_tags." matches"
      echohl None
      
      let complType=nr2char(getchar())
      if -1 != stridx("\<C-T>l\<C-N>\<C-P>np",complType)
	if complType =~ '[np]'  | let complType=nr2char(char2nr(complType)-96)
	elseif complType == "l" | return s:ShowList()
	endif
	let dir = (complType == "\<C-P>") ? -1 : 1
	normal x
      else
	return "\<right>".complType
      endif
    endwhile
  else	" context != 1
    return ''
  endif
endfunction
" }}}

" Function: s:CTRL_X() {{{
" The function wraps |i_CTRL-X| occurrences.
" (*) |i_CTRL-X_CTRL-T| is specialized to the insertion of PCGen tags according
"     to the syntax of the different PCGen lst files.
" (*) |i_CTRL-X_l| is added to propose the list of possible tags in a popup
"     menu.
" (*) Other invocations are redirected to their default behaviour.
" NB: Interresting sequences to note with PCGen :
" (*) |i_CTRL-X_CTRL-D| searchs for definitions ('DEFINE:xxx' tag)
" (*) |i_CTRL-X_CTRL-F| searchs for file names
function! s:CTRL_X()
  let s:alts = '' | let s:cur = 0
  while 1
    let complType=nr2char(getchar())
    if -1 != stridx("l\<C-T>\<C-X>",complType)
      if     complType == "l"      | return s:ShowList()
      elseif complType == "\<C-T>" | return s:GetAlt(1)
      elseif complType == "\<C-X>" 
	echohl StatusLineNC
	echo "\r-- mode ^X (/l/^E/^Y/^L/^]/^F/^I/^K/^D/^V/^N/^P/)"
	echohl None
	" else
      endif
    else
      return "\<c-x>".complType
    endif
  endwhile
endfunction
" }}}

inoremap <buffer> <C-X>		<c-r>=<sid>CTRL_X()<cr>
inoremap <buffer> <C-SPACE>	<c-r>=<sid>GetAlt(1)<cr>

" -- Completion }}}
" -- Moves & others {{{
nnoremap <buffer> <tab> /..$\\|$\\|\t[^\t]/s+1<cr>
nnoremap <buffer> <S-tab> ?..$\\|$\\|\t[^\t]?s+1<cr>
inoremap <buffer> <C-tab> <c-o>/..$\\|$\\|\t[^\t]/s+1<cr>
inoremap <buffer> <C-S-tab> <c-o>?..$\\|$\\|\t[^\t]?s+1<cr>

nnoremap <buffer> <localleader>w	:set wrap!<cr>
" inoremap <C-X>w		<C-o>:set wrap!<cr>

if has('menu') && has ('gui_running')
  nnoremenu 50.200 &PCGen.&Next\ field<tab>\<TAB\>			<TAB>
  nnoremenu 50.200 &PCGen.&Prev\ field<tab>\<S-TAB\>			<S-TAB>
  inoremenu 50.210 &PCGen.&Next\ field\ (insert)<tab>\<C-TAB\>		<C-TAB>
  inoremenu 50.210 &PCGen.&Prev\ field\ (insert)<tab>\<C-S-TAB\>	<C-S-TAB>
  amenu     50.299 &PCGen.--	<C-L>
  exe 'anoremenu 50.300 &PCGen.&Toogle\ wrap<tab>'
	\ .escape(maplocalleader, '\ ')."w	:set wrap!<cr>"
endif
" -- Moves }}}
" Mappings }}}
" }}}
"=============================================================================
" call confirm('done', 'ok')
"=============================================================================
"EOF	vim: ts=8 noet tw=80 sw=2 sts=0 fdm=marker 

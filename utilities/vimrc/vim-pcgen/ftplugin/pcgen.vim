" pcgen.vim - Editing PCGen LST files made easy
" Maintainer:   Henk Slaaf <henk@henkslaaf.nl>
" Version:      0.1

if exists("b:did_ftplugin")
	finish
endif
let b:did_ftplugin = 1
let b:did_conversion = 0


""" Automatically rewrite to tabbed format on write if we converted to multi-line
autocmd BufWritePre <buffer> call pcgen#multiLineToTabbed()


""" Function that set the syntax highlighting in tabbed format and hide it in
""" multi-line format

function! pcgen#setTabbedOptions() " {{{
	setlocal list lcs=eol:¶,tab:»-,trail:·
	setlocal noautoindent
endfunction " }}}

function! pcgen#setMultiLineOptions() " {{{
	setlocal autoindent
endfunction


""" Functions to convert between formats

function! pcgen#multiLineToTabbed() " {{{
	if !has("perl")
		echoerr("You need Vim compiled with Perl support")
		return
	endif

	if !b:did_conversion
		return
	endif

	perl <<EOF
		# Get the current buffer contents
		my @lines = $main::curbuf->Get(0 .. $main::curbuf->Count());
		my $contents = join "\n", @lines;

		# Replace all newlines that are followed by a tab character by just the tab character
		# This places all keywords in the line of the object identifier (tab-based syntax)
		$contents =~ s/\n(?!\n)//msg;

		# Replace all double newlines with single newlines, just as they were
		$contents =~ s/\n\n/\n/msg;

		# Then resplit the lines so we can add them to the buffer
		my @contents = split /\n/, $contents;

		# Replace the entire buffer
		$main::curbuf->Delete(1, $main::curbuf->Count());
		$main::curbuf->Append(0, @contents);
EOF
	let b:did_conversion = 0
endfunction " }}}

function! pcgen#tabbedToMultiLine() " {{{
	if !has("perl")
		echoerr("You need Vim compiled with Perl support")
		return
	endif

	if b:did_conversion
		return
	endif

	perl <<EOF
		# Get the current buffer contents
		my @lines = $main::curbuf->Get(0 .. $main::curbuf->Count());
		my $contents = join "\n", @lines;

		# Make sure newlines are doubled
		$contents =~ s/\n/\n\n/g; 

		# Then do the tab conversion magic
		# 1) Replace all tab characters by a newline and a tab: s/\t/\n\t/;
		# 2) Leave all additional tab characters at the end of the current line: s/(\t+)?/$1/;
		$contents =~ s/\t(\t+)?/$1\n\t/g;

		# Then resplit the lines so we can add them to the buffer
		my @contents = split /\n/, $contents;

		# Replace the entire buffer
		$main::curbuf->Delete(1, $main::curbuf->Count());
		$main::curbuf->Append(0, @contents);
EOF

	let b:did_conversion = 1
endfunction " }}}



""""""""""" Run

""" Set standard options

syntax on
setlocal list lcs=eol:¶,tab:»-,trail:·
setlocal nowrap

" We start in Tabbed mode
set noexpandtab
call pcgen#setTabbedOptions()


nmap <silent> <buffer> <F2> :call pcgen#tabbedToMultiLine()<CR>:call pcgen#setMultiLineOptions()<CR>
nmap <silent> <buffer> <F3> :call pcgen#multiLineToTabbed()<CR>:call pcgen#setTabbedOptions()<CR>
imap <silent> <buffer> <F4> PREABILITY:1,CATEGORY=Special Ability,

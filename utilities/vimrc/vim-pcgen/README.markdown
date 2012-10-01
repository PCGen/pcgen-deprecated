# pcgen.vim #

This is a VIM helper to edit oldskool PCGen files. It allows you to convert between multi-line object files and tab-based object files and back by pressing <F2> and <F3>.

### Features ###

1. Converts tab-delimited files to multi-line files by pressing `F2`; converts back when pressing `F3`; protects against accidental double conversion.
2. Shows tab characters using special syntax (`»---`). This helps to differentiate between the two classes and helps you spotting inserting accidental spaces where tabs are needed when copying or such.
3. Shows trailing white-space.
4. Writes out tab-delimited files on buffer write, so no more forgetting to convert back.
5. Now preserves multible tabs so prettylst.pl prettifying is preserved. This keeps the patches clean.
6. Knows to set specific options that are helpful in the two editing modes (autoindent, noexpandtab, etc).


### prerequisites ###

You need Vim compiled with Perl bindings to make this work.

# usage #

1. Use `F2` to convert from single-line object file to multi-line
2. Use `F3` to convert back
3. Use `F4` while editing to insert `PREABILITY:1,CATEGORY=Special Ability,`.


# installation #

Install [Pathogen.vim](https://github.com/tpope/vim-pathogen "Pathogen.vim"):

Then run:

   ```bash
   cd ~/.vim/bundle/
   git clone https://github.com/pcgen/vim-pcgen.git
   ```

### installation sans git ###

1. Download the zip using the [download link](https://github.com/pcgen/vim-pcgen/downloads).
2. Unpack into your bundle dir.

### manual installation ###

1. Download the zip using the [download link])(https://github.com/pcgen/vim-pcgen/downloads).
2. Unpack the zip.
3. Alternatively, install ftdetect/pcgen.vim and ftplugin/pcgen.vim into your ftdetect and ftplugin dirs respectively.


# changelog #

2012-10-01

 * Move the Perl command execution to in-line code.

2012-09-28

 * On buffer write, convert back to tab-based files.
 * Keep the spaces that are added by prettylst at the end of lines when converting to multi-line objects. This keeps the diff smaller when converting back to tab-based files on write-out or when converting back using <F3>

2012-09-26

 * Add protection against converting tab-based files to multi-line multiple times.
 * Add distinct options for multi-line mode and tab-based mode. (autoindent in multi-line)

## todo ##

Add options for auto-reverting to tab-based lines on write-out.
Add options for keeping multible tabs at the end of the line on reverting to tab-based.

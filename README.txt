Example scripts for compiling and running the particular dummy parser code provided here.

You must provide equivalent executable bash scripts for your implementation that can be run on raptor, so that:

+ If the source language you have chosen is a compiled one (e.g., Java) the marker can compile your source from scratch simply by running:

    bash ./compile

The compile script must take no arguments and any path names should be relative to the directory/folder in which the compile script is located.

If your source language is not a compiled language you do not need to include the compile script.

+ To run the parser the marker must be able to simply run:

    bash ./parse file.dopl

where file.dopl could be any filename - relative or absolute.

Do not hard-code the DOPL source file in the assemble script.

The parse script must not call the compile script.  You should assume that your code has been compiled (if required) by the marker prior to running the parser.

If you have used Java, you may simply edit the provided compile and parse scripts to adjust the particular package and file names you have chosen.

djb/2021.12.20

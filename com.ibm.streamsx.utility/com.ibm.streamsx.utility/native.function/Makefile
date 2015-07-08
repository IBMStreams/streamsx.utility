# begin_generated_IBM_copyright_prolog                             
#                                                                  
# This is an automatically generated copyright prolog.             
# After initializing,  DO NOT MODIFY OR MOVE                       
# **************************************************************** 
# Licensed Materials - Property of IBM                             
# 5724-Y95                                                         
# (C) Copyright IBM Corp.  2015    All Rights Reserved.      
# US Government Users Restricted Rights - Use, duplication or      
# disclosure restricted by GSA ADP Schedule Contract with          
# IBM Corp.                                                        
#                                                                  
# end_generated_IBM_copyright_prolog                               
#
# =========== IMPORTANT ===========
# SPL native function support allows us to tap into the rich functions offered
# by the comprehensive SPL API. To do this, you need to #include "SPLFunctions.h"
# in your C++ files.  The invocation of dst-pkg-config.sh below will supply the
# -I lines to locate the SPL API
# =========== IMPORTANT ===========

SPL_PKGCFG=$(STREAMS_INSTALL)/bin/dst-pe-pkg-config.sh
SPL_PKG=dst-spl-pe-install
SPL_INCLUDE_OPTIONS = `$(SPL_PKGCFG) --cflags $(SPL_PKG)`

.PHONY: clean distclean force

all: lib/libutility.so 

utility.o: utility.cpp utility.h
	@echo Compiling 'utility.cpp' ...
	@g++ -Wall -fPIC $(SPL_INCLUDE_OPTIONS) -c utility.cpp

# Now create a C++ shared library using the object (.o) file created in the previous step.
# After creating the shared library (.so) file, delete the object (.o) file. We don't need that anymore.
lib/libutility.so: utility.o
	@-mkdir -p lib
	@echo Building C++ shared library '$@'
	@g++ -shared -o $@ $<

clean:
	rm -rf *.o lib

distclean: clean

# /*********************************************************************
#  * Copyright (C) 2015, International Business Machines Corporation
#  * All Rights Reserved
#  ********************************************************************/
# =========== IMPORTANT ===========
# # SPL native function support allows us to tap into the rich functions offered
# # by the comprehensive SPL API. To do this, you need to #include "SPLFunctions.h"
# # in your C++ files.  The invocation of dst-pkg-config.sh below will supply the
# # -I lines to locate the SPL API
# # =========== IMPORTANT ===========

SPL_PKGCFG=$(STREAMS_INSTALL)/bin/dst-pe-pkg-config.sh
SPL_PKG=dst-spl-pe-install
SPL_INCLUDE_OPTIONS = `$(SPL_PKGCFG) --cflags $(SPL_PKG)`

.PHONY : all clean

CFLAGS   = -fPIC -O3 -I ../include
LDFLAGS  = -shared -lnuma -lpthread 

SOURCES = $(shell echo *.cpp)
HEADERS = $(shell echo *.h)
OBJECTS = $(SOURCES:.cpp=.o)
LIBPATH = $(shell ../bin/archLevel libPath)

TARGET = libutility.so.1.0.0
SO_NAME= libutility.so

all: $(LIBPATH)/$(TARGET)

$(OBJECTS): $(SOURCES)
	$(CC) $(CFLAGS) $(SPL_INCLUDE_OPTIONS) -c $(SOURCES)

clean:
	rm -f $(OBJECTS) $(LIBPATH)/$(TARGET) $(LIBPATH)/$(SO_NAME)

$(LIBPATH)/$(TARGET) : $(OBJECTS)
	$(CC) -fPIC $(CFLAGS) $(SPL_INCLUDE_OPTIONS) $(OBJECTS) -o $@ $(LDFLAGS)
	ln -f -s $(TARGET) $(LIBPATH)/$(SO_NAME)



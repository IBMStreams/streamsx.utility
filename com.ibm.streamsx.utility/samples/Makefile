.PHONY: all standalone clean

SPLC_FLAGS ?=  $(OUTPUTDIR) -a -s -t ..
SPLC = $(STREAMS_INSTALL)/bin/sc
# SPL_MAIN_COMPOSITE = com.ibm.ssb.epc::MainP730
SPL_MAIN_COMPOSITE = affinity

all: standalone

standalone: 
	spl-make-toolkit -i ..
	$(SPLC) $(SPLC_FLAGS) -T -M $(SPL_MAIN_COMPOSITE)

clean: 
	$(SPLC) $(SPLC_FLAGS) -C -M $(SPL_MAIN_COMPOSITE)


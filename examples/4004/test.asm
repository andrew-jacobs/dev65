
		.code
		.org	0

		nop
		jcn	A0,label
		fim	0,127
		src	0
		fin	0
		jin	0
		jun	label
		jms	label
		inc	0
		isz	7,label
		add	0
		sub	0
		ld	0
		xch	0
		bbl	7
		ldm	-7
		
label:		wrm
		wmp
		wrr
		wpm
		wr0
		wr1
		wr2
		wr3
		sbm
		rdm
		rdr
		adm
		rd0
		rd1
		rd2
		rd3
		
		clb
		clc
		iac
		cmc
		cma
		ral
		rar
		tcc
		dac
		tcs
		stc
		daa
		kbp
		dcl
		
		.end
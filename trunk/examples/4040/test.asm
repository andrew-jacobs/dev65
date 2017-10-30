;===============================================================================
; As4040 Opcode Test
;-------------------------------------------------------------------------------
; Copyright (C),2016-2017 Andrew John Jacobs.
;
; This program is provided free of charge for educational purposes
;
; Redistribution and use in binary form without modification, is permitted
; provided that the above copyright notice, this list of conditions and the
; following disclaimer in the documentation and/or other materials provided
; with the distribution.
;
; THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS 'AS IS' AND ANY
; EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
; WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
; DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
; DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
; (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
; ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
; (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
; THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
;-------------------------------------------------------------------------------
; Notes:
;
;
;===============================================================================

		.code
		.org	0
		
		nop
		hlt
		bbs
		lcr
		or4
		or5
		an6
		an7
		db0
		db1
		sb0
		sb1
		ein
		din
		rpm		
		jcn	0,label
		jcn	1,label
		jcn	2,label
		jcn	3,label
		jcn	4,label
		jcn	5,label
		jcn	6,label
		jcn	7,label
		jcn	8,label
		jcn	7,label
		jcn	10,label
		jcn	11,label
		jcn	12,label
		jcn	13,label
		jcn	14,label
		jcn	15,label
		fim	0,12h
		src	0
		fim	1,12h
		src	1
		fim	2,12h
		src	2
		fim	3,12h
		src	3
		fim	4,12h
		src	4
		fim	5,12h
		src	5
		fim	6,12h
		src	6
		fim	7,12h
		src	7
		fin	0
		jin	0
		fin	1
		jin	1
		fin	2
		jin	2
		fin	3
		jin	3
		fin	4
		jin	4
		fin	5
		jin	5
		fin	6
		jin	6
		fin	7
		jin	7
		jun	label
		jms	label
		inc	0
		inc	1
		inc	2
		inc	3
		inc	4
		inc	5
		inc	6
		inc	7
		inc	8
		inc	9
		inc	10
		inc	11
		inc	12
		inc	13
		inc	14
		inc	15
		isz	0,label
		isz	1,label
		isz	2,label
		isz	3,label
		isz	4,label
		isz	5,label
		isz	6,label
		isz	7,label
		isz	8,label
		isz	9,label
		isz	10,label
		isz	11,label
		isz	12,label
		isz	13,label
		isz	14,label
		isz	15,label
		add	0
		add	1
		add	2
		add	3
		add	4
		add	5
		add	6
		add	7
		add	8
		add	9
		add	10
		add	11
		add	12
		add	13
		add	14
		add	15
		sub	0
		sub	1
		sub	2
		sub	3
		sub	4
		sub	5
		sub	6
		sub	7
		sub	8
		sub	9
		sub	10
		sub	11
		sub	12
		sub	13
		sub	14
		sub	15
		ld	0
		ld	1
		ld	2
		ld	3
		ld	4
		ld	5
		ld	6
		ld	7
		ld	8
		ld	9
		ld	10
		ld	11
		ld	12
		ld	13
		ld	14
		ld	15
		xch	0
		xch	1
		xch	2
		xch	3
		xch	4
		xch	5
		xch	6
		xch	7
		xch	8
		xch	9
		xch	10
		xch	11
		xch	12
		xch	13
		xch	14
		xch	15
		bbl	0
		bbl	1
		bbl	2
		bbl	3
		bbl	4
		bbl	5
		bbl	6
		bbl	7
		bbl	8
		bbl	9
		bbl	10
		bbl	11
		bbl	12
		bbl	13
		bbl	14
		bbl	15
		ldm	0
		ldm	1
		ldm	2
		ldm	3
		ldm	4
		ldm	5
		ldm	6
		ldm	7
		ldm	8
		ldm	9
		ldm	10
		ldm	11
		ldm	12
		ldm	13
		ldm	14
		ldm	15
		wrm
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
		
;-------------------------------------------------------------------------------

; Example from user guide

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
		
label:
		
		.end
;===============================================================================
; As8008 Opcode Test
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
; Register names are declared as constants in the assembler so by default
; must be in UPPER case. You can create lower case versions with equates, for
; example 'a .equ A'.
;
;===============================================================================;

		.code
		.org	0h
		
		adc	A
		adc	B
		adc	C
		adc	D
		adc	E
		adc	H
		adc	L
		adc	M
		aci	12h
		add	A
		add	B
		add	C
		add	D
		add	E
		add	H
		add	L
		add	M
		adi	12h
		ana	A
		ana	B
		ana	C
		ana	D
		ana	E
		ana	H
		ana	L
		ana	M
		ani	12h
		call	label
		cc	label
		cm	label
		cmp	A
		cmp	B
		cmp	C
		cmp	D
		cmp	E
		cmp	H
		cmp	L
		cmp	M
		cnc	label
		cnz	label
		cp	label
		cpe	label
		cpi	12h
		cpo	label
		cz	label
		dcr	B
		dcr	C
		dcr	D
		dcr	E
		dcr	H
		dcr	L
		dcr	M
		hlt
		in	0
		in	1
		in	2
		in	3
		in	4
		in	5
		in	6
		in	7
		inr	B
		inr	C
		inr	D
		inr	E
		inr	H
		inr	L
		inr	M
		jc	label
		jm	label
		jmp	label
		jnc	label
		jnz	label
		jp	label
		jpe	label
		jpo	label
		jz	label
		mov	A,A
		mov	A,B
		mov	A,C
		mov	A,D
		mov	A,E
		mov	A,H
		mov	A,L
		mov	A,M
		mov	B,A
		mov	B,B
		mov	B,C
		mov	B,D
		mov	B,E
		mov	B,H
		mov	B,L
		mov	B,M
		mov	C,A
		mov	C,B
		mov	C,C
		mov	C,D
		mov	C,E
		mov	C,H
		mov	C,L
		mov	C,M
		mov	D,A
		mov	D,B
		mov	D,C
		mov	D,D
		mov	D,E
		mov	D,H
		mov	D,L
		mov	D,M
		mov	E,A
		mov	E,B
		mov	E,C
		mov	E,D
		mov	E,E
		mov	E,H
		mov	E,L
		mov	E,M
		mov	H,A
		mov	H,B
		mov	H,C
		mov	H,D
		mov	H,E
		mov	H,H
		mov	H,L
		mov	H,M
		mov	L,A
		mov	L,B
		mov	L,C
		mov	L,D
		mov	L,E
		mov	L,H
		mov	L,L
		mov	L,M
		mov	M,A
		mov	M,B
		mov	M,C
		mov	M,D
		mov	M,E
		mov	M,H
		mov	M,L
		mov	M,M
		mvi	A, 12h
		mvi	B, 12h
		mvi	C, 12h
		mvi	D, 12h
		mvi	E, 12h
		mvi	H, 12h
		mvi	L, 12h
		mvi	M, 12h
		ora	A
		ora	B
		ora	C
		ora	D
		ora	E
		ora	H
		ora	L
		ora	M
		ori	12h
		out	8
		out	9
		out	10
		out	11
		out	12
		out	13
		out	14
		out	15
		out	16
		out	17
		out	18
		out	19
		out	20
		out	21
		out	22
		out	23
		out	24
		out	25
		out	26
		out	27
		out	28
		out	29
		out	30
		out	31
		ral
		rar
		rc
		ret
		rlc
		rm
		rnc
		rnz
		rp
		rpo
		rpe
		rrc
		rst	0
		rst	1
		rst	2
		rst	3
		rst	4
		rst	5
		rst	6
		rst	7
		rz
		sbb	A
		sbb	B
		sbb	C
		sbb	D
		sbb	E
		sbb	H
		sbb	L
		sbb	M
		sbi	12h
		sub	A
		sub	B
		sub	C
		sub	D
		sub	E
		sub	H
		sub	L
		sub	M
		sui	12h
		xra	A
		xra	B
		xra	C
		xra	D
		xra	E
		xra	H
		xra	L
		xra	M
		xri	12h
		
label:
		
		.byte	1,2,3
		.word	1,2,3		
		
		.end
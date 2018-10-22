;===============================================================================
; As740 Opcode Test
;-------------------------------------------------------------------------------
; Copyright (C),2016-2018 Andrew John Jacobs.
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

SPC		.equ	$ff12		; Special page address $FF00-$FFFF

		.page0
		.org	$12

ZPG		.space	1

		.bss
		.org	$1234
		
ABS		.space	1


		.data
		
		.byte	1,2,3,4
		.byte	"abcde"
		.word	1,2,3,4

;==============================================================================
; Opcodes
;------------------------------------------------------------------------------

		.code
		.org	$f000

Test:
		adc	#ZPG
		adc	ZPG
		adc	ZPG,x
		adc	ABS
		adc	ABS,x
		adc	ABS,y
		adc	(ZPG,x)
		adc	(ZPG),y
		
		and	#ZPG
		and	ZPG
		and	ZPG,x
		and	ABS
		and	ABS,x
		and	ABS,y
		and	(ZPG,x)
		and	(ZPG),y
		
		asl	A
		asl	ZPG
		asl	ZPG,x
		asl	ABS
		asl	ABS,x

.BBC
		bbc	0,A,.BBC
		bbc	1,A,.BBC
		bbc	2,A,.BBC
		bbc	3,A,.BBC
		bbc	4,A,.BBC
		bbc	5,A,.BBC
		bbc	6,A,.BBC
		bbc	7,A,.BBC
		bbc	0,ZPG,.BBC
		bbc	1,ZPG,.BBC
		bbc	2,ZPG,.BBC
		bbc	3,ZPG,.BBC
		bbc	4,ZPG,.BBC
		bbc	5,ZPG,.BBC
		bbc	6,ZPG,.BBC
		bbc	7,ZPG,.BBC
		
.BBS
		bbs	0,A,.BBS
		bbs	1,A,.BBS
		bbs	2,A,.BBS
		bbs	3,A,.BBS
		bbs	4,A,.BBS
		bbs	5,A,.BBS
		bbs	6,A,.BBS
		bbs	7,A,.BBS
		bbs	0,ZPG,.BBS
		bbs	1,ZPG,.BBS
		bbs	2,ZPG,.BBS
		bbs	3,ZPG,.BBS
		bbs	4,ZPG,.BBS
		bbs	5,ZPG,.BBS
		bbs	6,ZPG,.BBS
		bbs	7,ZPG,.BBS
		
		bcc	.
		
		bcs	.
		
		beq	.
		
		bit	ZPG
		bit	ABS
		
		bmi	.
		
		bne	.
		
		bpl	.
		
		bra	.
		
		brk
		brk	#12
		
		bvc	.
		
		bvs	.
		
		clb	0,A
		clb	1,A
		clb	2,A
		clb	3,A
		clb	4,A
		clb	5,A
		clb	6,A
		clb	7,A
		clb	0,ZPG
		clb	1,ZPG
		clb	2,ZPG
		clb	3,ZPG
		clb	4,ZPG
		clb	5,ZPG
		clb	6,ZPG
		clb	7,ZPG
		
		clc
		
		cld
		
		cli
		
		clt
		
		clv
		
		cmp	#ZPG
		cmp	ZPG
		cmp	ZPG,x
		cmp	ABS
		cmp	ABS,x
		cmp	ABS,y
		cmp	(ZPG,x)
		cmp	(ZPG),y
		
		com	ZPG
		
		cpx	#ZPG
		cpx	ZPG
		cpx	ABS

		cpy	#ZPG
		cpy	ZPG
		cpy	ABS

		dec	A
		dec	ZPG
		dec	ZPG,x
		dec	ABS
		dec	ABS,x

		dex
		
		dey
		
		div	ZPG,x
		
		eor	#ZPG
		eor	ZPG
		eor	ZPG,x
		eor	ABS
		eor	ABS,x
		eor	ABS,y
		eor	(ZPG,x)
		eor	(ZPG),y

		inc	A
		inc	ZPG
		inc	ZPG,x
		inc	ABS
		inc	ABS,x

		inx
		
		iny
		
		jmp	ABS
		jmp	(ABS)
		jmp	(ZPG)

		jsr	ABS
		jsr	SPC
		jsr	(ZPG)

		lda	#ZPG
		lda	ZPG
		lda	ZPG,x
		lda	ABS
		lda	ABS,x
		lda	ABS,y
		lda	(ZPG,x)
		lda	(ZPG),y

		ldm	#$ff,ZPG

		ldx	#ZPG
		ldx	ZPG
		ldx	ZPG,y
		ldx	ABS
		ldx	ABS,y
		
		ldy	#ZPG
		ldy	ZPG
		ldy	ZPG,x
		ldy	ABS
		ldy	ABS,x
		
		lsr	A
		lsr	ZPG
		lsr	ZPG,x
		lsr	ABS
		lsr	ABS,x
		
		mul	ZPG,x
		
		nop
		
		ora	#ZPG
		ora	ZPG
		ora	ZPG,x
		ora	ABS
		ora	ABS,x
		ora	ABS,y
		ora	(ZPG,x)
		ora	(ZPG),y

		pha
		
		php
		
		pla
		
		plp

		rol	A
		rol	ZPG
		rol	ZPG,x
		rol	ABS
		rol	ABS,x

		ror	A
		ror	ZPG
		ror	ZPG,x
		ror	ABS
		ror	ABS,x
		
		rrf	ZPG
		
		rti
		
		rts
		
		sbc	#ZPG
		sbc	ZPG
		sbc	ZPG,x
		sbc	ABS
		sbc	ABS,x
		sbc	ABS,y
		sbc	(ZPG,x)
		sbc	(ZPG),y
		
		seb	0,A
		seb	1,A
		seb	2,A
		seb	3,A
		seb	4,A
		seb	5,A
		seb	6,A
		seb	7,A
		seb	0,ZPG
		seb	1,ZPG
		seb	2,ZPG
		seb	3,ZPG
		seb	4,ZPG
		seb	5,ZPG
		seb	6,ZPG
		seb	7,ZPG

		sec
		
		sed
		
		sei
		
		set		
		
		sta	ZPG
		sta	ZPG,x
		sta	ABS
		sta	ABS,x
		sta	ABS,y
		sta	(ZPG,x)
		sta	(ZPG),y
		
		stp
		
		stx	ZPG
		stx	ZPG,y
		stx	ABS
		
		sty	ZPG
		sty	ZPG,x
		sty	ABS
		
		tax
		
		tay
		
		tst	ZPG
		
		tsx
		
		txa
		
		txs
		
		tya
		
		wit
		
		
;==============================================================================
; Structured Programming
;------------------------------------------------------------------------------

		if cc
		 nop
		endif
		
		repeat
		 nop
		forever
		
		repeat
		 nop
		until mi
		
		repeat
		 if cc
		  break
		 endif
		 break pl
		 nop
		forever
		
		repeat
		 if pl
		  continue
		 endif
		 continue mi
		forever
		
		repeat
		 inc a
		until eq
		
		while ne
		 inc a
		endw
		
		.end
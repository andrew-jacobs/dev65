;===============================================================================
; AsKb1 Opcode Test
;-------------------------------------------------------------------------------
; Copyright (C),2018 Andrew John Jacobs.
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
; This generates more code than a KENBAK can actually hold. 
;
;===============================================================================;
				
		.code

		.org	0
		
REG_A		.space	1
REG_B		.space	1
REG_X		.space	1
REG_P		.byte	START

START:
		add	a =DATA
		add	a DATA
		add	a DATA,X
		add	a (DATA)
		add	a (DATA),X
		add	b =DATA
		add	b DATA
		add	b DATA,X
		add	b (DATA)
		add	b (DATA),X
		add	x =DATA
		add	x DATA
		add	x DATA,X
		add	x (DATA)
		add	x (DATA),X

		sub	a =DATA
		sub	a DATA
		sub	a DATA,X
		sub	a (DATA)
		sub	a (DATA),X
		sub	b =DATA
		sub	b DATA
		sub	b DATA,X
		sub	b (DATA)
		sub	b (DATA),X
		sub	x =DATA
		sub	x DATA
		sub	x DATA,X
		sub	x (DATA)
		sub	x (DATA),X

		load	a =DATA
		load	a DATA
		load	a DATA,X
		load	a (DATA)
		load	a (DATA),X
		load	b =DATA
		load	b DATA
		load	b DATA,X
		load	b (DATA)
		load	b (DATA),X
		load	x =DATA
		load	x DATA
		load	x DATA,X
		load	x (DATA)
		load	x (DATA),X
		
		store	a =DATA
		store	a DATA
		store	a DATA,X
		store	a (DATA)
		store	a (DATA),X
		store	b =DATA
		store	b DATA
		store	b DATA,X
		store	b (DATA)
		store	b (DATA),X
		store	x =DATA
		store	x DATA
		store	x DATA,X
		store	x (DATA)
		store	x (DATA),X
		
		and	=DATA
		and	DATA
		and	DATA,X
		and	(DATA)
		and	(DATA),X

		or	=DATA
		or	DATA
		or	DATA,X
		or	(DATA)
		or	(DATA),X

		lneg	=DATA
		lneg	DATA
		lneg	DATA,X
		lneg	(DATA)
		lneg	(DATA),X

		jpd	a<>0 DATA
		jpd	a=0 DATA
		jpd	a<0 DATA
		jpd	a>0 DATA
		jpd	a>=0 DATA
		jpd	b<>0 DATA
		jpd	b=0 DATA
		jpd	b<0 DATA
		jpd	b>0 DATA
		jpd	b>=0 DATA
		jpd	x<>0 DATA
		jpd	x=0 DATA
		jpd	x<0 DATA
		jpd	x>0 DATA
		jpd	x>=0 DATA
		
		jpi	a<>0 DATA
		jpi	a=0 DATA
		jpi	a<0 DATA
		jpi	a>0 DATA
		jpi	a>=0 DATA
		jpi	b<>0 DATA
		jpi	b=0 DATA
		jpi	b<0 DATA
		jpi	b>0 DATA
		jpi	b>=0 DATA
		jpi	x<>0 DATA
		jpi	x=0 DATA
		jpi	x<0 DATA
		jpi	x>0 DATA
		jpi	x>=0 DATA
		
		jmd	a<>0 DATA
		jmd	a=0 DATA
		jmd	a<0 DATA
		jmd	a>0 DATA
		jmd	a>=0 DATA
		jmd	b<>0 DATA
		jmd	b=0 DATA
		jmd	b<0 DATA
		jmd	b>0 DATA
		jmd	b>=0 DATA
		jmd	x<>0 DATA
		jmd	x=0 DATA
		jmd	x<0 DATA
		jmd	x>0 DATA
		jmd	x>=0 DATA
		
		jmi	a<>0 DATA
		jmi	a=0 DATA
		jmi	a<0 DATA
		jmi	a>0 DATA
		jmi	a>=0 DATA
		jmi	b<>0 DATA
		jmi	b=0 DATA
		jmi	b<0 DATA
		jmi	b>0 DATA
		jmi	b>=0 DATA
		jmi	x<>0 DATA
		jmi	x=0 DATA
		jmi	x<0 DATA
		jmi	x>0 DATA
		jmi	x>=0 DATA
		
		jpd	DATA
		jpi	DATA
		jmd	DATA
		jmi	DATA
		
		skp0	0 DATA
		skp0	1 DATA
		skp0	2 DATA
		skp0	3 DATA
		skp0	4 DATA
		skp0	5 DATA
		skp0	6 DATA
		skp0	7 DATA
		skp1	0 DATA
		skp1	1 DATA
		skp1	2 DATA
		skp1	3 DATA
		skp1	4 DATA
		skp1	5 DATA
		skp1	6 DATA
		skp1	7 DATA

		set0	0 DATA
		set0	1 DATA
		set0	2 DATA
		set0	3 DATA
		set0	4 DATA
		set0	5 DATA
		set0	6 DATA
		set0	7 DATA
		set1	0 DATA
		set1	1 DATA
		set1	2 DATA
		set1	3 DATA
		set1	4 DATA
		set1	5 DATA
		set1	6 DATA
		set1	7 DATA
		
		sftl	a 1
		sftl	a 2
		sftl	a 3
		sftl	a 4
		sftl	b 1
		sftl	b 2
		sftl	b 3
		sftl	b 4
		
		sftr	a 1
		sftr	a 2
		sftr	a 3
		sftr	a 4
		sftr	b 1
		sftr	b 2
		sftr	b 3
		sftr	b 4
		
		rotl	a 1
		rotl	a 2
		rotl	a 3
		rotl	a 4
		rotl	b 1
		rotl	b 2
		rotl	b 3
		rotl	b 4
		
		rotr	a 1
		rotr	a 2
		rotr	a 3
		rotr	a 4
		rotr	b 1
		rotr	b 2
		rotr	b 3
		rotr	b 4
		
		noop
		
		halt

		.org	200O
		
REG_OUT		.space	1
FLG_A		.space	1
FLG_B		.space	1
FLG_X		.space	1

DATA		.space	1

		.org	377O
		
REG_IN		.space	1

		.end
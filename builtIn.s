	.file	"builtIn.c"
	.text
	.section	.rodata
	.align	2
.LC0:
	.string	"%s"
	.text
	.align	2
	.globl	builtIn_print
	.type	builtIn_print, @function
builtIn_print:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	lw	a1,-20(s0)
	lui	a5,%hi(.LC0)
	addi	a0,a5,%lo(.LC0)
	call	printf
	nop
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	builtIn_print, .-builtIn_print
	.align	2
	.globl	builtIn_println
	.type	builtIn_println, @function
builtIn_println:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	lw	a0,-20(s0)
	call	puts
	nop
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	builtIn_println, .-builtIn_println
	.section	.rodata
	.align	2
.LC1:
	.string	"%d"
	.text
	.align	2
	.globl	builtIn_printInt
	.type	builtIn_printInt, @function
builtIn_printInt:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	lw	a1,-20(s0)
	lui	a5,%hi(.LC1)
	addi	a0,a5,%lo(.LC1)
	call	printf
	nop
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	builtIn_printInt, .-builtIn_printInt
	.section	.rodata
	.align	2
.LC2:
	.string	"%d\n"
	.text
	.align	2
	.globl	builtIn_printlnInt
	.type	builtIn_printlnInt, @function
builtIn_printlnInt:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	lw	a1,-20(s0)
	lui	a5,%hi(.LC2)
	addi	a0,a5,%lo(.LC2)
	call	printf
	nop
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	builtIn_printlnInt, .-builtIn_printlnInt
	.align	2
	.globl	builtIn_getInt
	.type	builtIn_getInt, @function
builtIn_getInt:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	addi	a5,s0,-20
	mv	a1,a5
	lui	a5,%hi(.LC1)
	addi	a0,a5,%lo(.LC1)
	call	scanf
	lw	a5,-20(s0)
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	builtIn_getInt, .-builtIn_getInt
	.align	2
	.globl	builtIn_getString
	.type	builtIn_getString, @function
builtIn_getString:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	li	a0,1000
	call	malloc
	mv	a5,a0
	sw	a5,-20(s0)
	lw	a1,-20(s0)
	lui	a5,%hi(.LC0)
	addi	a0,a5,%lo(.LC0)
	call	scanf
	lw	a5,-20(s0)
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	builtIn_getString, .-builtIn_getString
	.align	2
	.globl	builtIn_toString
	.type	builtIn_toString, @function
builtIn_toString:
	addi	sp,sp,-96
	sw	ra,92(sp)
	sw	s0,88(sp)
	addi	s0,sp,96
	sw	a0,-84(s0)
	lw	a5,-84(s0)
	bne	a5,zero,.L10
	li	a0,2
	call	malloc
	mv	a5,a0
	sw	a5,-36(s0)
	lw	a5,-36(s0)
	li	a4,48
	sb	a4,0(a5)
	lw	a5,-36(s0)
	addi	a5,a5,1
	sb	zero,0(a5)
	lw	a5,-36(s0)
	j	.L18
.L10:
	sw	zero,-20(s0)
	sw	zero,-24(s0)
	lw	a5,-84(s0)
	bge	a5,zero,.L13
	li	a5,1
	sw	a5,-24(s0)
	lw	a5,-84(s0)
	neg	a5,a5
	sw	a5,-84(s0)
	j	.L13
.L14:
	lw	a5,-20(s0)
	addi	a4,a5,1
	sw	a4,-20(s0)
	lw	a3,-84(s0)
	li	a4,10
	rem	a4,a3,a4
	slli	a5,a5,2
	addi	a3,s0,-16
	add	a5,a3,a5
	sw	a4,-64(a5)
	lw	a4,-84(s0)
	li	a5,10
	div	a5,a4,a5
	sw	a5,-84(s0)
.L13:
	lw	a5,-84(s0)
	bgt	a5,zero,.L14
	lw	a5,-20(s0)
	addi	a4,a5,1
	lw	a5,-24(s0)
	add	a5,a4,a5
	mv	a0,a5
	call	malloc
	mv	a5,a0
	sw	a5,-32(s0)
	lw	a5,-24(s0)
	beq	a5,zero,.L15
	lw	a5,-32(s0)
	li	a4,45
	sb	a4,0(a5)
.L15:
	sw	zero,-28(s0)
	j	.L16
.L17:
	lw	a4,-20(s0)
	lw	a5,-28(s0)
	sub	a5,a4,a5
	addi	a5,a5,-1
	slli	a5,a5,2
	addi	a4,s0,-16
	add	a5,a4,a5
	lw	a5,-64(a5)
	andi	a4,a5,0xff
	lw	a3,-24(s0)
	lw	a5,-28(s0)
	add	a5,a3,a5
	mv	a3,a5
	lw	a5,-32(s0)
	add	a5,a5,a3
	addi	a4,a4,48
	andi	a4,a4,0xff
	sb	a4,0(a5)
	lw	a5,-28(s0)
	addi	a5,a5,1
	sw	a5,-28(s0)
.L16:
	lw	a4,-28(s0)
	lw	a5,-20(s0)
	blt	a4,a5,.L17
	lw	a4,-24(s0)
	lw	a5,-20(s0)
	add	a5,a4,a5
	mv	a4,a5
	lw	a5,-32(s0)
	add	a5,a5,a4
	sb	zero,0(a5)
	lw	a5,-32(s0)
.L18:
	mv	a0,a5
	lw	ra,92(sp)
	lw	s0,88(sp)
	addi	sp,sp,96
	jr	ra
	.size	builtIn_toString, .-builtIn_toString
	.align	2
	.globl	builtIn_string_length
	.type	builtIn_string_length, @function
builtIn_string_length:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	lw	a0,-20(s0)
	call	strlen
	mv	a5,a0
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	builtIn_string_length, .-builtIn_string_length
	.align	2
	.globl	builtIn_string_substring
	.type	builtIn_string_substring, @function
builtIn_string_substring:
	addi	sp,sp,-48
	sw	ra,44(sp)
	sw	s0,40(sp)
	addi	s0,sp,48
	sw	a0,-36(s0)
	sw	a1,-40(s0)
	sw	a2,-44(s0)
	li	a0,1000
	call	malloc
	mv	a5,a0
	sw	a5,-20(s0)
	lw	a5,-40(s0)
	lw	a4,-36(s0)
	add	a3,a4,a5
	lw	a4,-44(s0)
	lw	a5,-40(s0)
	sub	a5,a4,a5
	mv	a2,a5
	mv	a1,a3
	lw	a0,-20(s0)
	call	memcpy
	lw	a4,-44(s0)
	lw	a5,-40(s0)
	sub	a5,a4,a5
	mv	a4,a5
	lw	a5,-20(s0)
	add	a5,a5,a4
	sb	zero,0(a5)
	lw	a5,-20(s0)
	mv	a0,a5
	lw	ra,44(sp)
	lw	s0,40(sp)
	addi	sp,sp,48
	jr	ra
	.size	builtIn_string_substring, .-builtIn_string_substring
	.align	2
	.globl	builtIn_string_parseInt
	.type	builtIn_string_parseInt, @function
builtIn_string_parseInt:
	addi	sp,sp,-48
	sw	ra,44(sp)
	sw	s0,40(sp)
	addi	s0,sp,48
	sw	a0,-36(s0)
	addi	a5,s0,-20
	mv	a2,a5
	lui	a5,%hi(.LC1)
	addi	a1,a5,%lo(.LC1)
	lw	a0,-36(s0)
	call	sscanf
	lw	a5,-20(s0)
	mv	a0,a5
	lw	ra,44(sp)
	lw	s0,40(sp)
	addi	sp,sp,48
	jr	ra
	.size	builtIn_string_parseInt, .-builtIn_string_parseInt
	.align	2
	.globl	builtIn_string_ord
	.type	builtIn_string_ord, @function
builtIn_string_ord:
	addi	sp,sp,-32
	sw	s0,28(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	sw	a1,-24(s0)
	lw	a5,-24(s0)
	lw	a4,-20(s0)
	add	a5,a4,a5
	lbu	a5,0(a5)
	mv	a0,a5
	lw	s0,28(sp)
	addi	sp,sp,32
	jr	ra
	.size	builtIn_string_ord, .-builtIn_string_ord
	.align	2
	.globl	builtIn_string_add
	.type	builtIn_string_add, @function
builtIn_string_add:
	addi	sp,sp,-48
	sw	ra,44(sp)
	sw	s0,40(sp)
	addi	s0,sp,48
	sw	a0,-36(s0)
	sw	a1,-40(s0)
	li	a0,1000
	call	malloc
	mv	a5,a0
	sw	a5,-20(s0)
	lw	a0,-36(s0)
	call	strlen
	mv	a5,a0
	sw	a5,-24(s0)
	lw	a5,-24(s0)
	mv	a2,a5
	lw	a1,-36(s0)
	lw	a0,-20(s0)
	call	memcpy
	lw	a5,-24(s0)
	lw	a4,-20(s0)
	add	a5,a4,a5
	sb	zero,0(a5)
	lw	a1,-40(s0)
	lw	a0,-20(s0)
	call	strcat
	lw	a5,-20(s0)
	mv	a0,a5
	lw	ra,44(sp)
	lw	s0,40(sp)
	addi	sp,sp,48
	jr	ra
	.size	builtIn_string_add, .-builtIn_string_add
	.align	2
	.globl	builtIn_string_Less
	.type	builtIn_string_Less, @function
builtIn_string_Less:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	sw	a1,-24(s0)
	lw	a1,-24(s0)
	lw	a0,-20(s0)
	call	strcmp
	mv	a5,a0
	srli	a5,a5,31
	andi	a5,a5,0xff
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	builtIn_string_Less, .-builtIn_string_Less
	.align	2
	.globl	builtIn_string_Greater
	.type	builtIn_string_Greater, @function
builtIn_string_Greater:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	sw	a1,-24(s0)
	lw	a1,-24(s0)
	lw	a0,-20(s0)
	call	strcmp
	mv	a5,a0
	sgt	a5,a5,zero
	andi	a5,a5,0xff
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	builtIn_string_Greater, .-builtIn_string_Greater
	.align	2
	.globl	builtIn_string_LessEqual
	.type	builtIn_string_LessEqual, @function
builtIn_string_LessEqual:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	sw	a1,-24(s0)
	lw	a1,-24(s0)
	lw	a0,-20(s0)
	call	strcmp
	mv	a5,a0
	slti	a5,a5,1
	andi	a5,a5,0xff
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	builtIn_string_LessEqual, .-builtIn_string_LessEqual
	.align	2
	.globl	builtIn_string_GreaterEqual
	.type	builtIn_string_GreaterEqual, @function
builtIn_string_GreaterEqual:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	sw	a1,-24(s0)
	lw	a1,-24(s0)
	lw	a0,-20(s0)
	call	strcmp
	mv	a5,a0
	not	a5,a5
	srli	a5,a5,31
	andi	a5,a5,0xff
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	builtIn_string_GreaterEqual, .-builtIn_string_GreaterEqual
	.align	2
	.globl	builtIn_string_Equal
	.type	builtIn_string_Equal, @function
builtIn_string_Equal:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	sw	a1,-24(s0)
	lw	a1,-24(s0)
	lw	a0,-20(s0)
	call	strcmp
	mv	a5,a0
	seqz	a5,a5
	andi	a5,a5,0xff
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	builtIn_string_Equal, .-builtIn_string_Equal
	.align	2
	.globl	builtIn_string_NotEqual
	.type	builtIn_string_NotEqual, @function
builtIn_string_NotEqual:
	addi	sp,sp,-32
	sw	ra,28(sp)
	sw	s0,24(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	sw	a1,-24(s0)
	lw	a1,-24(s0)
	lw	a0,-20(s0)
	call	strcmp
	mv	a5,a0
	snez	a5,a5
	andi	a5,a5,0xff
	mv	a0,a5
	lw	ra,28(sp)
	lw	s0,24(sp)
	addi	sp,sp,32
	jr	ra
	.size	builtIn_string_NotEqual, .-builtIn_string_NotEqual
	.ident	"GCC: (GNU) 10.1.0"

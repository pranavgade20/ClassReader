package io.github.pranavgade20.classexplorer;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BytecodeInstruction {
    Opcode opcode;
    byte[] params;
    public BytecodeInstruction(Opcode opcode, byte[] params) {
        this.opcode = opcode;
        this.params = params;
    }

    static List<BytecodeInstruction> getInstructionsFromBytes(byte[] bytes) {
        LinkedList<BytecodeInstruction> ret = new LinkedList<>();

        int i = 0;
        while (i < bytes.length) {
            Opcode opcode = Opcode.getFrom(bytes[i++]);
            byte[] params;

            switch (opcode) {
                case ALOAD:
                case ASTORE:
                case BIPUSH:
                case DLOAD:
                case DSTORE:
                case FLOAD:
                case FSTORE:
                case ILOAD:
                case ISTORE:
                case LLOAD:
                case LDC:
                case LSTORE:
                case NEWARRAY:
                case RET:
                    params = new byte[1];
                    params[0] = bytes[i++];
                    break;
                case ANEWARRAY:
                case CHECKCAST:
                case GETFIELD:
                case GETSTATIC:
                case GOTO:
                case IF_ACMPEQ:
                case IF_ACMPNE:
                case IF_ICMPEQ:
                case IF_ICMPGE:
                case IF_ICMPGT:
                case IF_ICMPLE:
                case IF_ICMPLT:
                case IF_ICMPNE:
                case IFEQ:
                case IFGE:
                case IFGT:
                case IFLE:
                case IFLT:
                case IFNE:
                case IFNONNULL:
                case IFNULL:
                case IINC:
                case INSTANCEOF:
                case INVOKESPECIAL:
                case INVOKESTATIC:
                case INVOKEVIRTUAL:
                case JSR:
                case LDC_W:
                case LDC2_W:
                case NEW:
                case PUTFIELD:
                case PUTSTATIC:
                case SIPUSH:
                    params = new byte[2];
                    params[0] = bytes[i++];
                    params[1] = bytes[i++];
                    break;
                case MULTIANEWARRAY:
                    params = new byte[3];
                    params[0] = bytes[i++];
                    params[1] = bytes[i++];
                    params[2] = bytes[i++];
                    break;
                case GOTO_W:
                case INVOKEDYNAMIC:
                case INVOKEINTERFACE:
                case JSR_W:
                    params = new byte[4];
                    params[0] = bytes[i++];
                    params[1] = bytes[i++];
                    params[2] = bytes[i++];
                    params[3] = bytes[i++];
                    break;
                case LOOKUPSWITCH:
                    while (i%4 != 0) i++; // padding to 4 byte border
                    int default_val = (bytes[i++] << 24) | (bytes[i++] << 16) | (bytes[i++] << 8) | bytes[i++];
                    int num_pairs = (bytes[i++] << 24) | (bytes[i++] << 16) | (bytes[i++] << 8) | bytes[i++];
                    ByteBuffer parameters = ByteBuffer.allocate(num_pairs*8 + 8);
                    parameters.putInt(0, default_val);
                    parameters.putInt(4, num_pairs);
                    parameters.position(8);
                    parameters.put(bytes, i, num_pairs*8); // 4 byte for ints, 4 for offeset
                    params = parameters.array();
                    i += num_pairs*8;
                    break;
                case TABLESWITCH:
                    while (i%4 != 0) i++; // padding to 4 byte border
                    default_val = (bytes[i++] << 24) | (bytes[i++] << 16) | (bytes[i++] << 8) | bytes[i++];
                    int low = (bytes[i++] << 24) | (bytes[i++] << 16) | (bytes[i++] << 8) | bytes[i++];
                    int high = (bytes[i++] << 24) | (bytes[i++] << 16) | (bytes[i++] << 8) | bytes[i++];
                    parameters = ByteBuffer.allocate((high-low+1)*4 + 12);
                    parameters.putInt(0, default_val);
                    parameters.putInt(4, low);
                    parameters.putInt(8, high);
                    parameters.position(12);
                    parameters.put(bytes, i, (high-low+1)*4); // jump offsets
                    params = parameters.array();
                    i += (high-low+1)*4;
                    break;
                case WIDE:
                    Opcode op = Opcode.getFrom(bytes[i++]);
                    if (op == Opcode.IINC) {
                        params = new byte[5];
                        params[0] = (byte) Opcode.IINC.opcode;
                        params[1] = bytes[i++];
                        params[2] = bytes[i++];
                        params[3] = bytes[i++];
                        params[4] = bytes[i++];
                    } else {
                        params = new byte[3];
                        params[0] = (byte) op.opcode;
                        params[1] = bytes[i++];
                        params[2] = bytes[i++];
                    }
                    break;
                default:
                    params = new byte[0];
                    break;
            }

            ret.add(new BytecodeInstruction(opcode, params));
        }

        return ret;
    }

    @Override
    public String toString() {
        return opcode + " " + Arrays.toString(params).replaceAll("[\\],\\[]", "");
    }

    public void write(DataOutput output) throws IOException {
        output.writeByte(opcode.opcode);
        output.write(params);
    }
}

enum Opcode {
    AALOAD(50),
    AASTORE(83),
    ACONST_NULL(1),
    ALOAD(25),
    ALOAD_0(42),
    ALOAD_1(43),
    ALOAD_2(44),
    ALOAD_3(45),
    ANEWARRAY(189),
    ARETURN(176),
    ARRAYLENGTH(190),
    ASTORE(58),
    ASTORE_0(75),
    ASTORE_1(76),
    ASTORE_2(77),
    ASTORE_3(78),
    ATHROW(191),
    BALOAD(51),
    BASTORE(84),
    BIPUSH(16),
    BREAKPOINT(202),
    CALOAD(52),
    CASTORE(85),
    CHECKCAST(192),
    D2F(144),
    D2I(142),
    D2L(143),
    DADD(99),
    DALOAD(49),
    DASTORE(82),
    DCMPG(152),
    DCMPL(151),
    DCONST_0(14),
    DCONST_1(15),
    DDIV(111),
    DLOAD(24),
    DLOAD_0(38),
    DLOAD_1(39),
    DLOAD_2(40),
    DLOAD_3(41),
    DMUL(107),
    DNEG(119),
    DREM(115),
    DRETURN(175),
    DSTORE(57),
    DSTORE_0(71),
    DSTORE_1(72),
    DSTORE_2(73),
    DSTORE_3(74),
    DSUB(103),
    DUP(89),
    DUP_X1(90),
    DUP_X2(91),
    DUP2(92),
    DUP2_X1(93),
    DUP2_X2(94),
    F2D(141),
    F2I(139),
    F2L(140),
    FADD(98),
    FALOAD(48),
    FASTORE(81),
    FCMPG(150),
    FCMPL(149),
    FCONST_0(11),
    FCONST_1(12),
    FCONST_2(13),
    FDIV(110),
    FLOAD(23),
    FLOAD_0(34),
    FLOAD_1(35),
    FLOAD_2(36),
    FLOAD_3(37),
    FMUL(106),
    FNEG(118),
    FREM(114),
    FRETURN(174),
    FSTORE(56),
    FSTORE_0(67),
    FSTORE_1(68),
    FSTORE_2(69),
    FSTORE_3(70),
    FSUB(102),
    GETFIELD(180),
    GETSTATIC(178),
    GOTO(167),
    GOTO_W(200),
    I2B(145),
    I2C(146),
    I2D(135),
    I2F(134),
    I2L(133),
    I2S(147),
    IADD(96),
    IALOAD(46),
    IAND(126),
    IASTORE(79),
    ICONST_M1(2),
    ICONST_0(3),
    ICONST_1(4),
    ICONST_2(5),
    ICONST_3(6),
    ICONST_4(7),
    ICONST_5(8),
    IDIV(108),
    IF_ACMPEQ(165),
    IF_ACMPNE(166),
    IF_ICMPEQ(159),
    IF_ICMPGE(162),
    IF_ICMPGT(163),
    IF_ICMPLE(164),
    IF_ICMPLT(161),
    IF_ICMPNE(160),
    IFEQ(153),
    IFGE(156),
    IFGT(157),
    IFLE(158),
    IFLT(155),
    IFNE(154),
    IFNONNULL(199),
    IFNULL(198),
    IINC(132),
    ILOAD(21),
    ILOAD_0(26),
    ILOAD_1(27),
    ILOAD_2(28),
    ILOAD_3(29),
    IMPDEP1(254),
    IMPDEP2(255),
    IMUL(104),
    INEG(116),
    INSTANCEOF(193),
    INVOKEDYNAMIC(186),
    INVOKEINTERFACE(185),
    INVOKESPECIAL(183),
    INVOKESTATIC(182),
    INVOKEVIRTUAL(184),
    IOR(128),
    IREM(112),
    IRETURN(172),
    ISHL(120),
    ISHR(122),
    ISTORE(54),
    ISTORE_0(59),
    ISTORE_1(60),
    ISTORE_2(61),
    ISTORE_3(62),
    ISUB(100),
    IUSHR(124),
    IXOR(130),
    JSR(168),
    JSR_W(201),
    L2D(138),
    L2F(137),
    L2I(136),
    LADD(97),
    LALOAD(47),
    LAND(127),
    LASTORE(80),
    LCMP(148),
    LCONST_0(9),
    LCONST_1(10),
    LDC(18),
    LDC_W(19),
    LDC2_W(20),
    LDIV(109),
    LLOAD(22),
    LLOAD_0(30),
    LLOAD_1(31),
    LLOAD_2(32),
    LLOAD_3(33),
    LMUL(105),
    LNEG(117),
    LOOKUPSWITCH(171),
    LOR(129),
    LREM(113),
    LRETURN(173),
    LSHL(121),
    LSHR(123),
    LSTORE(55),
    LSTORE_0(63),
    LSTORE_1(64),
    LSTORE_2(65),
    LSTORE_3(66),
    LSUB(101),
    LUSHR(125),
    LXOR(131),
    MONITORENTER(194),
    MONITOREXIT(195),
    MULTIANEWARRAY(197),
    NEW(187),
    NEWARRAY(188),
    NOP(0),
    POP(87),
    POP2(88),
    PUTFIELD(181),
    PUTSTATIC(179),
    RET(169),
    RETURN(177),
    SALOAD(53),
    SASTORE(86),
    SIPUSH(17),
    SWAP(95),
    TABLESWITCH(170),
    WIDE(196),
    INVALID_OPCODE(256);

    int opcode;
    static Opcode[] opcodes;
    static {
        opcodes = new Opcode[256];
        Arrays.fill(opcodes, INVALID_OPCODE);
        Opcode[] values = Opcode.values();
        for (int i = 0, valuesLength = values.length-1; i < valuesLength; i++) {
            Opcode opcode = values[i];
            opcodes[opcode.opcode] = opcode;
        }
    }

    Opcode(int opcode) {
        this.opcode = opcode;
    }

    static Opcode getFrom(byte opcode) {
        return opcodes[0xFF&opcode];
    }
}
package io.github.pranavgade20.classexplorer;

public class AccessFlags {
    private short flags;

    public AccessFlags(short flags) {
        this.flags = flags;
    }

    public static AccessFlags fromFlags(short flags) {
        return new AccessFlags(flags);
    }

    public short getFlags() {
        return flags;
    }

    /**
     * Declared public; may be accessed from outside its package.
     * @return is it public
     */
    public boolean isPublic() {
        return (flags&0x0001) > 0;
    }

    public void setIsPublic(boolean isPublic) {
        if (isPublic) flags |= 0x0001;
        else flags &= ~0x0001;
    }

    /**
     * Declared private; usable only within the defining class.
     * @return is it private
     */
    public boolean isPrivate() {
        return (flags&0x0002) > 0;
    }

    public void setIsPrivate(boolean isPrivate) {
        if (isPrivate) flags |= 0x0002;
        else flags &= ~0x0002;
    }

    /**
     * Declared protected; may be accessed within subclasses.
     * @return is it protected
     */
    public boolean isProtected() {
        return (flags&0x0004) > 0;
    }

    public void setIsProtected(boolean isProtected) {
        if (isProtected) flags |= 0x0004;
        else flags &= ~0x0004;
    }

    /**
     * Declared static
     * @return is it static
     */
    public boolean isStatic() {
        return (flags&0x0008) > 0;
    }

    public void setIsStatic(boolean isStatic) {
        if (isStatic) flags |= 0x0008;
        else flags &= ~0x0008;
    }

    /**
     * Declared final; no subclasses allowed, or never directly assigned to after object construction
     * @return is the class final
     */
    public boolean isFinal() {
        return (flags&0x0010) > 0;
    }

    public void setIsFinal(boolean isFinal) {
        if (isFinal) flags |= 0x0010;
        else flags &= ~0x0010;
    }

    /**
     * Declared volatile; cannot be cached.
     * @return is this volatile
     */
    public boolean isVolatile() {
        return (flags&0x0040) > 0;
    }

    public void setIsVolatile(boolean isVolatile) {
        if (isVolatile) flags |= 0x0040;
        else flags &= ~0x0040;
    }

    /**
     * Declared transient; not written or read by a persistent object manager.
     * @return is this volatile
     */
    public boolean isTransient() {
        return (flags&0x0080) > 0;
    }

    public void setIsTransient(boolean isTransient) {
        if (isTransient) flags |= 0x0080;
        else flags &= ~0x0080;
    }

    /**
     * Treat superclass methods specially when invoked by the invokespecial instruction.
     * @return are the superclass methods treated specially
     */
    public boolean isSuper() {
        return (flags&0x0040) > 0;
    }

    public void setIsSuper(boolean isSuper) {
        if (isSuper) flags |= 0x0040;
        else flags &= ~0x0040;
    }

    /**
     * Is an interface, not a class.
     * @return is this an interface
     */
    public boolean isInterface() {
        return (flags&0x0200) > 0;
    }

    public void setIsInterface(boolean isInterface) {
        if (isInterface) flags |= 0x0200;
        else flags &= ~0x0200;
    }

    /**
     * Declared abstract; must not be instantiated.
     * @return is this declared to be abstract
     */
    public boolean isAbstract() {
        return (flags&0x0400) > 0;
    }

    public void setIsAbstract(boolean isAbstract) {
        if (isAbstract) flags |= 0x0400;
        else flags &= ~0x0400;
    }

    /**
     * Declared synthetic; not present in the source code.
     * @return is this declared to be synthetic
     */
    public boolean isSynthetic() {
        return (flags&0x1000) > 0;
    }

    public void setIsSynthetic(boolean isSynthetic) {
        if (isSynthetic) flags |= 0x1000;
        else flags &= ~0x1000;
    }

    /**
     * Declared as an annotation type.
     * @return is this declared as an annotation
     */
    public boolean isAnnotation() {
        return (flags&0x2000) > 0;
    }

    public void setIsAnnotation(boolean isAnnotation) {
        if (isAnnotation) flags |= 0x2000;
        else flags &= ~0x2000;
    }

    /**
     * Declared as an enum type.
     * @return is this declared as an enum
     */
    public boolean isEnum() {
        return (flags&0x4000) > 0;
    }

    public void setIsEnum(boolean isEnum) {
        if (isEnum) flags |= 0x4000;
        else flags &= ~0x4000;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        if(isPublic()) ret.append("Public,");
        if(isPrivate()) ret.append("Private,");
        if(isProtected()) ret.append("Protected,");
        if(isStatic()) ret.append("Static,");
        if(isFinal()) ret.append("Final,");
        if(isVolatile()) ret.append("Volatile,");
        if(isTransient()) ret.append("Transient,");
        if(isSuper()) ret.append("Super,");
        if(isInterface()) ret.append("Interface,");
        if(isAbstract()) ret.append("Abstract,");
        if(isSynthetic()) ret.append("Synthetic,");
        if(isAnnotation()) ret.append("Annotation,");
        if(isEnum()) ret.append("Enum,");

        if (ret.length() == 0) {
            ret.append("no flags set ");
        }
        return ret.replace(ret.length()-1, ret.length(), "").toString();
    }
}

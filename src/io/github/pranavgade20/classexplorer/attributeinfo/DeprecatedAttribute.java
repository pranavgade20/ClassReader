package io.github.pranavgade20.classexplorer.attributeinfo;

import io.github.pranavgade20.classexplorer.Klass;

import java.io.DataInput;

public class DeprecatedAttribute extends AttributeInfo {
    // represents Deprecated_attribute
    DeprecatedAttribute(AttributeInfo parent, DataInput classStream, Klass klass) {
        super(parent);
    }
}

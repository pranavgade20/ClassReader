package io.github.pranavgade20.classexplorer.attributeinfo;

import io.github.pranavgade20.classexplorer.Klass;

import java.io.DataInput;

public class SyntheticAttribute extends AttributeInfo {
    // represents Synthetic_attribute
    SyntheticAttribute(AttributeInfo parent, DataInput classStream, Klass klass) {
        super(parent);
    }
}

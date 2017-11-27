package com.darcy.Scheme2016FineGrained.test;

import org.ujmp.core.*;
import org.ujmp.core.bigdecimalmatrix.*;
import org.ujmp.core.bigintegermatrix.*;
import org.ujmp.core.booleanmatrix.*;
import org.ujmp.core.bytearraymatrix.*;
import org.ujmp.core.bytematrix.*;
import org.ujmp.core.charmatrix.*;
import org.ujmp.core.doublematrix.*;
import org.ujmp.core.floatmatrix.*;
import org.ujmp.core.genericmatrix.*;
import org.ujmp.core.graphmatrix.DefaultGraphMatrix;
import org.ujmp.core.graphmatrix.GraphMatrix;
import org.ujmp.core.intmatrix.*;
import org.ujmp.core.longmatrix.*;
import org.ujmp.core.objectmatrix.*;
import org.ujmp.core.shortmatrix.*;
import org.ujmp.core.stringmatrix.*;

/*
 * author: darcy
 * date: 2017/11/5 20:31
 * description: 
*/
public class GUITest {
	public static void main(String[] args) {
		// create a GraphMatrix with Strings as nodes and Doubles as edges
		GraphMatrix<String, Double> graphMatrix = new DefaultGraphMatrix<String, Double>();
		graphMatrix.setLabel("Interface Inheritance Graph");

// collect all matrix interfaces from UJMP
		Class<?>[] classArray = new Class[]{DenseMatrix.class, DenseMatrix2D.class, Matrix.class, Matrix2D.class,
				SparseMatrix.class, SparseMatrix2D.class, BaseBigDecimalMatrix.class, BigDecimalMatrix2D.class,
				DenseBigDecimalMatrix.class, DenseBigDecimalMatrix2D.class, SparseBigDecimalMatrix.class,
				SparseBigDecimalMatrix2D.class, BigIntegerMatrix.class, BigIntegerMatrix2D.class,
				DenseBigIntegerMatrix.class, DenseBigIntegerMatrix2D.class, SparseBigIntegerMatrix.class,
				SparseBigIntegerMatrix2D.class, BooleanMatrix.class, BooleanMatrix2D.class, DenseBooleanMatrix.class,
				DenseBooleanMatrix2D.class, SparseBooleanMatrix.class, SparseBooleanMatrix2D.class,
				ByteArrayMatrix.class, ByteArrayMatrix2D.class, DenseByteArrayMatrix.class,
				DenseByteArrayMatrix2D.class, SparseByteArrayMatrix.class, SparseByteArrayMatrix2D.class,
				ByteMatrix.class, ByteMatrix2D.class, DenseByteMatrix.class, DenseByteMatrix2D.class,
				SparseByteMatrix.class, SparseByteMatrix2D.class, CharMatrix.class, CharMatrix2D.class,
				DenseCharMatrix.class, DenseCharMatrix2D.class, SparseCharMatrix.class, SparseCharMatrix2D.class,
				DoubleMatrix.class, DoubleMatrix2D.class, DenseDoubleMatrix.class, DenseDoubleMatrix2D.class,
				SparseDoubleMatrix.class, SparseDoubleMatrix2D.class, FloatMatrix.class, FloatMatrix2D.class,
				DenseFloatMatrix.class, DenseFloatMatrix2D.class, SparseFloatMatrix.class, SparseFloatMatrix2D.class,
				GenericMatrix.class, GenericMatrix2D.class, DenseGenericMatrix.class, DenseGenericMatrix2D.class,
				SparseGenericMatrix.class, SparseGenericMatrix2D.class, IntMatrix.class, IntMatrix2D.class,
				DenseIntMatrix.class, DenseIntMatrix2D.class, SparseIntMatrix.class, SparseIntMatrix2D.class,
				LongMatrix.class, LongMatrix2D.class, DenseLongMatrix.class, DenseLongMatrix2D.class,
				SparseLongMatrix.class, SparseLongMatrix2D.class, ObjectMatrix.class, ObjectMatrix2D.class,
				DenseObjectMatrix.class, DenseObjectMatrix2D.class, SparseObjectMatrix.class,
				SparseObjectMatrix2D.class, ShortMatrix.class, ShortMatrix2D.class, DenseShortMatrix.class,
				DenseShortMatrix2D.class, SparseShortMatrix.class, SparseShortMatrix2D.class, StringMatrix.class,
				StringMatrix2D.class, DenseStringMatrix.class, DenseStringMatrix2D.class, SparseStringMatrix.class,
				SparseStringMatrix2D.class};

    // find out how interfaces extend one another
		for (Class<?> c1 : classArray) {
			for (Class<?> c2 : classArray) {
				if (c2.getSuperclass() == c1) {
					// add edge when class2 extends class1
					graphMatrix.setEdge(1.0, c1.getSimpleName(), c2.getSimpleName());
				}
				for (Class<?> c3 : c2.getInterfaces()) {
					if (c1 == c3) {
						// add edge when class2 implements class1
						graphMatrix.setEdge(1.0, c1.getSimpleName(), c2.getSimpleName());
					}
				}
			}
		}

    // show on screen
		graphMatrix.showGUI();
	}
}

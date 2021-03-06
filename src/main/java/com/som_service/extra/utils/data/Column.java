/*
 * MIT License
 * 
 * Copyright (c) 2017 Eugene Dementiev
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.som_service.extra.utils.data;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Eugene Dementiev
 */
public class Column {
	public final Cell[] cells;
	public final int column;
	
	public Column(int height, int column){
		this.cells = new Cell[height];
		this.column = column;
		
		for(int r = 0; r < height; r++){
			cells[r] = new Cell(this, r);
		}
		
		//normalise();
	}
	
	public double get_sum(){
		double sum = 0;
		for (Cell cell: cells){
			sum += cell.get_value();
		}
		return sum;
	}
	
	public void normalise(){
		
		double sum =this.get_sum();
		
		if (sum == 0){
			for (Cell cell: cells){
				cell.set_value(1);
				sum++;
			}
		}
		
		for (Cell cell: cells){
			cell.set_value(cell.get_value()/sum);
		}
	}
	
	public String toString(){
		return StringUtils.join(cells,"\n");
	}
	
	public Cell[] get_cells(){
		return cells;
	}

	public void round(int precision){
		for (Cell cell: cells){
			cell.set_value(Math.round(cell.get_value()*Math.pow(10, precision))/Math.pow(10, precision));
		}
	}
	
	public void replace_zeros(double replacement){
		for (Cell cell: cells){
			if (cell.get_value() == 0d){
				cell.set_value(replacement);
			}
		}
	}
	
	/**
	 * Replaces the values in the column according to a geometric progression
	 * @param start initial value
	 * @param ratio the progression ratio
	 * @param growing describes the direction of the progression<br/>true if the cells[0] is set to the [start], false if the cells[cells.length-1] is set to the [start]
	 * @return this column
	 */
	public Column geometric(double start, double ratio, boolean growing){
		int index = 1;
		cells[0].set_value(start);
		if (!growing){
			index = cells.length-2;
			cells[cells.length-1].set_value(start);
		}
		
		int direction = growing?1:-1;
		
		do{
			Cell cell_prev = cells[index-direction];
			cells[index].set_value(cell_prev.get_value()*ratio);
			index += direction;
		}
		while(index >= 0 && index < cells.length);
		
		return this;
	}
	
	/**
	 * Inserts the value at position, shifting the values after this position in the direction of position sign
	 * @param value value to be inserted
	 * @param position value will be inserted at this position; if zero or positive, values will be shifted forward; if negative - backward
	 * @return this column
	 */
	public Column shift_with(double value, int position){
		int direction = position<1?-1:1;
		int pos = Math.abs(position);
		int index = pos+direction;
		while(index >= 0 && index < cells.length){
			Cell cell_prev = cells[index-direction];
			cells[index].set_value(cell_prev.get_value());
			index += direction;
		}
		cells[pos].set_value(value);
		
		return this;
	}
}

/** ===================================================================
 *
 * @PROJECT.FULLNAME@ @VERSION@ License.
 *
 * Copyright (c) @YEAR@ L2FProd.com.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by L2FProd.com
 *        (http://www.L2FProd.com/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "@PROJECT.FULLNAME@", "@PROJECT.SHORTNAME@" and "L2FProd.com" must not
 *    be used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please
 *    contact info@L2FProd.com.
 *
 * 5. Products derived from this software may not be called "@PROJECT.SHORTNAME@"
 *    nor may "@PROJECT.SHORTNAME@" appear in their names without prior written
 *    permission of L2FProd.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL L2FPROD.COM OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package com.l2fprod.common.propertysheet;

import com.l2fprod.common.beans.ExtendedPropertyDescriptor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * PropertyDescriptorAdapter.<br>
 *
 */
class PropertyDescriptorAdapter extends AbstractProperty {

  private PropertyDescriptor descriptor;
  
  public PropertyDescriptorAdapter(PropertyDescriptor descriptor) {
    this.descriptor = descriptor;
  }

  public String getName() {
    return descriptor.getName();
  }
  
  public String getDisplayName() {
    return descriptor.getDisplayName();
  }
  
  public String getShortDescription() {
    return descriptor.getShortDescription();
  }

  public Class getType() {
    return descriptor.getPropertyType();
  }

  public void readFromObject(Object object) {
    try {
      Method method = descriptor.getReadMethod();
      
      // OVERRIDE: Added null check.
      if (method != null)
          setValue(method.invoke(object, null));
      
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public void writeToObject(Object object) {
    try {
      Method method = descriptor.getWriteMethod();
      if (method != null) {
        method.invoke(object, new Object[]{getValue()});
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public boolean isEditable() {
    return descriptor.getWriteMethod() != null;
  }

  public String getCategory() {
    if (descriptor instanceof ExtendedPropertyDescriptor) {
      return ((ExtendedPropertyDescriptor)descriptor).getCategory();
    } else {
      return null;
    }
  }
  
}

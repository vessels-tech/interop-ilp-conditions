/*
 * PDP FSP API (Implementation Friendly Version)
 * DRAFT FOR INTERNAL REVIEW ONLY - Based on API Definition.docx updated on 2017-07-06 - Implementation friendly version. [Changes from pervious version (2.7) - maxLength changed to 48 from 32 for IlpCondition and IlpFulfilment Types.]
 *
 * OpenAPI spec version: 2.8
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.ilp.conditions.models.pdp;

import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

/**
 * Data model for the complex type ExtensionList
 */
public class ExtensionList {

  private List<Extension> extension = new ArrayList<Extension>();

  public ExtensionList extension(List<Extension> extension) {
    this.extension = extension;
    return this;
  }

  public ExtensionList addExtensionItem(Extension extensionItem) {
    this.extension.add(extensionItem);
    return this;
  }

   /**
   * Number of Extension elements
   * @return extension
  **/
  public List<Extension> getExtension() {
    return extension;
  }

  public void setExtension(List<Extension> extension) {
    this.extension = extension;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExtensionList extensionList = (ExtensionList) o;
    return Objects.equals(this.extension, extensionList.extension);
  }

  @Override
  public int hashCode() {
    return Objects.hash(extension);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExtensionList {\n");
    
    sb.append("    extension: ").append(toIndentedString(extension)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
}
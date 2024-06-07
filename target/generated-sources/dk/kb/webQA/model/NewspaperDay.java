package dk.kb.webQA.model;

import dk.kb.webQA.model.NewspaperEdition;
import dk.kb.webQA.model.Note;
import dk.kb.webQA.model.SlimBatch;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
import javax.validation.Valid;

import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;


public class NewspaperDay  {
  
  @ApiModelProperty(value = "")
  @Valid
  private SlimBatch batch;

  @ApiModelProperty(value = "")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate date;

  @ApiModelProperty(value = "")
  @Valid
  private List<Note> notes = null;

  @ApiModelProperty(value = "")
  @Valid
  private List<NewspaperEdition> editions = null;
 /**
  * Get batch
  * @return batch
  */
  @JsonProperty("batch")
  public SlimBatch getBatch() {
    return batch;
  }

  /**
   * Sets the <code>batch</code> property.
   */
 public void setBatch(SlimBatch batch) {
    this.batch = batch;
  }

  /**
   * Sets the <code>batch</code> property.
   */
  public NewspaperDay batch(SlimBatch batch) {
    this.batch = batch;
    return this;
  }

 /**
  * Get date
  * @return date
  */
  @JsonProperty("date")
  public LocalDate getDate() {
    return date;
  }

  /**
   * Sets the <code>date</code> property.
   */
 public void setDate(LocalDate date) {
    this.date = date;
  }

  /**
   * Sets the <code>date</code> property.
   */
  public NewspaperDay date(LocalDate date) {
    this.date = date;
    return this;
  }

 /**
  * Get notes
  * @return notes
  */
  @JsonProperty("notes")
  public List<Note> getNotes() {
    return notes;
  }

  /**
   * Sets the <code>notes</code> property.
   */
 public void setNotes(List<Note> notes) {
    this.notes = notes;
  }

  /**
   * Sets the <code>notes</code> property.
   */
  public NewspaperDay notes(List<Note> notes) {
    this.notes = notes;
    return this;
  }

  /**
   * Adds a new item to the <code>notes</code> list.
   */
  public NewspaperDay addNotesItem(Note notesItem) {
    this.notes.add(notesItem);
    return this;
  }

 /**
  * Get editions
  * @return editions
  */
  @JsonProperty("editions")
  public List<NewspaperEdition> getEditions() {
    return editions;
  }

  /**
   * Sets the <code>editions</code> property.
   */
 public void setEditions(List<NewspaperEdition> editions) {
    this.editions = editions;
  }

  /**
   * Sets the <code>editions</code> property.
   */
  public NewspaperDay editions(List<NewspaperEdition> editions) {
    this.editions = editions;
    return this;
  }

  /**
   * Adds a new item to the <code>editions</code> list.
   */
  public NewspaperDay addEditionsItem(NewspaperEdition editionsItem) {
    this.editions.add(editionsItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NewspaperDay {\n");
    
    sb.append("    batch: ").append(toIndentedString(batch)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    notes: ").append(toIndentedString(notes)).append("\n");
    sb.append("    editions: ").append(toIndentedString(editions)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private static String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}


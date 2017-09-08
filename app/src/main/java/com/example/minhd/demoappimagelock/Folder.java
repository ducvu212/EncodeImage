package com.example.minhd.demoappimagelock;

import java.io.Serializable;

public class Folder
  implements Serializable
{
  private String dateCreated;
  private String idFolder;
  private int imageFolder;
  private boolean isLock;
  private String nameFolder;

  public Folder(String paramString1, String paramString2, int paramInt)
  {
    this.idFolder = paramString1;
    this.nameFolder = paramString2;
    this.dateCreated = TimeConvert.convertMilisecondsToDate(paramString1);
    this.imageFolder = paramInt;
    this.isLock = false;
  }

  public String getDateCreated()
  {
    return this.dateCreated;
  }

  public String getIdFolder()
  {
    return this.idFolder;
  }

  public int getImageFolder()
  {
    return this.imageFolder;
  }

  public String getNameFolder()
  {
    return this.nameFolder;
  }

  public boolean isLock()
  {
    return this.isLock;
  }

  public void setDateCreated(String paramString)
  {
    this.dateCreated = paramString;
  }

  public void setIdFolder(String paramString)
  {
    this.idFolder = paramString;
  }

  public void setImageFolder(int paramInt)
  {
    this.imageFolder = paramInt;
  }

  public void setNameFolder(String paramString)
  {
    this.nameFolder = paramString;
  }
}

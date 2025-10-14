package isometries


object IsometryFunction {
  def rotateExpand: Isometry =  Rotation()
  def rotateNoExpand: Isometry = new Rotation() with NonExpandable with NonTransparent

  def axialReflection : Isometry = new AxialReflection() with Expandable with NonTransparent

  def centralSymmetry:  Isometry =  (new Rotation() with Expandable >>> new Rotation() with Expandable)

  def translate: Isometry = AxialReflection() >>> AxialReflection()



}

package com.azavea.maml.ast

import geotrellis.vector.Geometry

import java.util.UUID


case class GeomLiteral[Geometry](geom: Geometry) extends Source {
  val kind = MamlKind.Geom
}


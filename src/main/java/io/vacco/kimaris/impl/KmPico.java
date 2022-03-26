package io.vacco.kimaris.impl;

import io.vacco.kimaris.io.KmIo;
import io.vacco.kimaris.schema.*;
import java.net.URL;

public class KmPico {

  public static KmCascadeSet loadCascades() {
    var cs = new KmCascadeSet();
    cs.face = KmIo.unpackFaceCascade(KmPico.class.getResource("/io/vacco/kimaris/facefinder"));
    cs.pupil = KmIo.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/puploc"));
    cs.lp38 = KmIo.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp38"));
    cs.lp42 = KmIo.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp42"));
    cs.lp44 = KmIo.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp44"));
    cs.lp46 = KmIo.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp46"));
    cs.lp81 = KmIo.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp81"));
    cs.lp82 = KmIo.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp82"));
    cs.lp84 = KmIo.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp84"));
    cs.lp93 = KmIo.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp93"));
    cs.lp312 = KmIo.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp312"));
    return cs;
  }
/*
  public static void detectFaces(KmCascadeSet cs, KmScanParams cp, URL imageUrl) {
    KmDetFace.clusterDetections(KmDetFace.runCascade(cs.face, cp, KmIo.loadImage(imageUrl)), cp.iouThreshold)
        .stream()
        .filter(det -> det.q > cp.qThreshold)
        .map(det -> {
          var face = new KmFace();
          var pupLoc = KmCoord.from(
              face.loc.row -
          );

          face.loc = KmCoord.from(
              det.coord.col - det.coord.scale / 2,
              det.coord.row - det.coord.scale / 2,
              det.coord.scale
          );

          if len(det.puploc) > 0 && face.Scale > 50 {
            rect := image.Rect(
                face.Col-face.Scale/2,
                face.Row-face.Scale/2,
                face.Col+face.Scale/2,
                face.Row+face.Scale/2,
                )
            rows, cols := rect.Max.X-rect.Min.X, rect.Max.Y-rect.Min.Y
            ctx := gg.NewContext(rows, cols)
            faceZone := ctx.Image()

            // left eye
            puploc = &pigo.Puploc{
              Row:      face.Row - int(0.075*float32(face.Scale)),
                  Col:      face.Col - int(0.175*float32(face.Scale)),
                  Scale:    float32(face.Scale) * 0.25,
                  Perturbs: perturb,
            }
            leftEye := plc.RunDetector(*puploc, *imgParams, det.angle, false)
            if leftEye.Row > 0 && leftEye.Col > 0 {
              if det.angle > 0 {
                drawEyeDetectionMarker(ctx,
                    float64(cols/2-(face.Col-leftEye.Col)),
                    float64(rows/2-(face.Row-leftEye.Row)),
                    float64(leftEye.Scale),
                    color.RGBA{R: 255, G: 0, B: 0, A: 255},
                det.markDetEyes,
						)
                angle := (det.angle * 180) / math.Pi
                rotated := imaging.Rotate(faceZone, 2*angle, color.Transparent)
                final := imaging.FlipH(rotated)

                dc.DrawImage(final, face.Col-face.Scale/2, face.Row-face.Scale/2)
              } else {
                drawEyeDetectionMarker(dc,
                    float64(leftEye.Col),
                    float64(leftEye.Row),
                    float64(leftEye.Scale),
                    color.RGBA{R: 255, G: 0, B: 0, A: 255},
                det.markDetEyes,
						)
              }
              eyesCoords = append(eyesCoords, coord{
                Col:   leftEye.Row,
                    Row:   leftEye.Col,
                    Scale: int(leftEye.Scale),
              })
            }

            // right eye
            puploc = &pigo.Puploc{
              Row:      face.Row - int(0.075*float32(face.Scale)),
                  Col:      face.Col + int(0.185*float32(face.Scale)),
                  Scale:    float32(face.Scale) * 0.25,
                  Perturbs: perturb,
            }

            rightEye := plc.RunDetector(*puploc, *imgParams, det.angle, false)
            if rightEye.Row > 0 && rightEye.Col > 0 {
              if det.angle > 0 {
                drawEyeDetectionMarker(ctx,
                    float64(cols/2-(face.Col-rightEye.Col)),
                    float64(rows/2-(face.Row-rightEye.Row)),
                    float64(rightEye.Scale),
                    color.RGBA{R: 255, G: 0, B: 0, A: 255},
                det.markDetEyes,
						)
                // convert radians to angle
                angle := (det.angle * 180) / math.Pi
                rotated := imaging.Rotate(faceZone, 2*angle, color.Transparent)
                final := imaging.FlipH(rotated)

                dc.DrawImage(final, face.Col-face.Scale/2, face.Row-face.Scale/2)
              } else {
                drawEyeDetectionMarker(dc,
                    float64(rightEye.Col),
                    float64(rightEye.Row),
                    float64(rightEye.Scale),
                    color.RGBA{R: 255, G: 0, B: 0, A: 255},
                det.markDetEyes,
						)
              }
              eyesCoords = append(eyesCoords, coord{
                Col:   rightEye.Row,
                    Row:   rightEye.Col,
                    Scale: int(rightEye.Scale),
              })
            }

            if len(det.flploc) > 0 {
              for _, eye := range eyeCascades {
                for _, flpc := range flpcs[eye] {
                  flp := flpc.GetLandmarkPoint(leftEye, rightEye, *imgParams, perturb, false)
                  if flp.Row > 0 && flp.Col > 0 {
                    drawEyeDetectionMarker(dc,
                        float64(flp.Col),
                        float64(flp.Row),
                        float64(flp.Scale*0.5),
                        color.RGBA{R: 0, G: 0, B: 255, A: 255},
                    false,
								)
                    landmarkCoords = append(landmarkCoords, coord{
                      Col:   flp.Row,
                          Row:   flp.Col,
                          Scale: int(flp.Scale),
                    })
                  }

                  flp = flpc.GetLandmarkPoint(leftEye, rightEye, *imgParams, perturb, true)
                  if flp.Row > 0 && flp.Col > 0 {
                    drawEyeDetectionMarker(dc,
                        float64(flp.Col),
                        float64(flp.Row),
                        float64(flp.Scale*0.5),
                        color.RGBA{R: 0, G: 0, B: 255, A: 255},
                    false,
								)
                    landmarkCoords = append(landmarkCoords, coord{
                      Col:   flp.Row,
                          Row:   flp.Col,
                          Scale: int(flp.Scale),
                    })
                  }
                }
              }

              for _, mouth := range mouthCascades {
                for _, flpc := range flpcs[mouth] {
                  flp := flpc.GetLandmarkPoint(leftEye, rightEye, *imgParams, perturb, false)
                  if flp.Row > 0 && flp.Col > 0 {
                    drawEyeDetectionMarker(dc,
                        float64(flp.Col),
                        float64(flp.Row),
                        float64(flp.Scale*0.5),
                        color.RGBA{R: 0, G: 0, B: 255, A: 255},
                    false,
								)
                    landmarkCoords = append(landmarkCoords, coord{
                      Col:   flp.Row,
                          Row:   flp.Col,
                          Scale: int(flp.Scale),
                    })
                  }
                }
              }
              flp := flpcs["lp84"][0].GetLandmarkPoint(leftEye, rightEye, *imgParams, perturb, true)
              if flp.Row > 0 && flp.Col > 0 {
                drawEyeDetectionMarker(dc,
                    float64(flp.Col),
                    float64(flp.Row),
                    float64(flp.Scale*0.5),
                    color.RGBA{R: 0, G: 0, B: 255, A: 255},
                false,
						)
                landmarkCoords = append(landmarkCoords, coord{
                  Col:   flp.Row,
                      Row:   flp.Col,
                      Scale: int(flp.Scale),
                })
              }
            }
          }
          detections = append(detections, detection{
            FacePoints:     *faceCoord,
                EyePoints:      eyesCoords,
                LandmarkPoints: landmarkCoords,
          })

          return face;
        });
  }
*/
}

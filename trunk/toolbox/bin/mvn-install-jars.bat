set lib=t:\workspaces\workspace-toolbox\toolbox\toolbox\lib
set sf=t:/workspaces/workspace-toolbox/toolbox/toolbox/settings.xml 


if exist %sf% set sa=--settings %sf% 

call mvn %sa% install:install-file -Dfile=%LIB%\acrobat.jar          -DartifactId=acrobat          -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\apple-mrj.jar        -DartifactId=apple-mrj        -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\calphahtml.jar       -DartifactId=calphahtml       -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\fhlaf.jar            -DartifactId=fhlaf            -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\hamsam.jar           -DartifactId=hamsam           -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\hippolf.jar          -DartifactId=hippolf          -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\infonodelf.jar       -DartifactId=infonodelf       -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\jarjar.jar           -DartifactId=jarjar           -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\jcommon.jar          -DartifactId=jcommon          -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\jedit-syntax.jar     -DartifactId=jedit-syntax     -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\jfreechart.jar       -DartifactId=jfreechart       -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\jode.jar             -DartifactId=jode             -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\jreversepro.jar      -DartifactId=jreversepro      -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\jtidy.jar            -DartifactId=jtidy            -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\jtoaster.jar         -DartifactId=jtoaster         -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\junit-addons.jar     -DartifactId=junit-addons     -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\kunststoff.jar       -DartifactId=kunststoff       -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\l2fprod-common.jar   -DartifactId=l2fprod-common   -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\liquidlf.jar         -DartifactId=liquidlf         -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\looks.jar            -DartifactId=looks            -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\matra.jar            -DartifactId=matra            -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\metouia.jar          -DartifactId=metouia          -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\multivalent.jar      -DartifactId=multivalent      -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\officelf.jar         -DartifactId=officelf         -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\oyoahalf.jar         -DartifactId=oyoahalf         -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\pollo.jar            -DartifactId=pollo            -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\prefuse.jar          -DartifactId=prefuse          -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\skinlf.jar           -DartifactId=skinlf           -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\squarenesslf.jar     -DartifactId=squarenesslf     -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\statcvs.jar          -DartifactId=statcvs          -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\systray4j.jar        -DartifactId=systray4j        -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\tinylaf.jar          -DartifactId=tinylaf          -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\toniclf.jar          -DartifactId=toniclf          -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
call mvn %sa% install:install-file -Dfile=%LIB%\xplookandfeel.jar    -DartifactId=xplookandfeel    -DgroupId=toolbox -Dversion=SNAPSHOT -Dpackaging=jar -DgeneratePom=true
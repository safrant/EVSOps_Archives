## Rob Wynne, LMCO
##
## MGED Conversion, Second pass
## Using the mapping file, replace all instances of rdf:ID, rdf:about, or
## rdf:resource with corresponding unique_identifier.
##
## 070808- Also, replace object, datatype, annotation, and functional
## properties with the unique_identifier where applicable.


open( $mgedinput, $ARGV[0] ) or die "Couldn't open MO file!\n";
open( $mapin, $ARGV[1] ) or die "Couldn't open map file!\n";
open( $header, $ARGV[2] ) or die "Couldn't open header file!\n";
open $out, '>', "test.txt" or die "Couldn't create test file\n";
@lines = <$mgedinput>;
@codes = <$mapin>;
@headers = <$header>;
$linecount = @lines;
$codecount = @code;
%mgedmap;

foreach(@codes) {
  if( $_ =~ /(.*)\t(.*)\n/ ) {
    $mgedmap{$1} = $2;
  }
}

foreach(@headers) {
  print $_;
}

foreach(@lines) {
  if( $_ =~ /(.*)(rdf:ID|rdf:about|rdf:resource)=\"(.*)\"(\s*\/?>\n)/ ) {
##   print $out $_;
    $begining = $1;
    $rdf = $2;
    $name = $3;
    $end = $4;
    $isReference = 0;
    #check for reference
    if($name =~ /#(.*)/ ) {
      $name = $1;
      $isReference = 1;
    }
    #replace the name with code, if available
    if( $begining =~ /(.*<)(.*)(\s)/ ) {
      $startsWith = $1;
      $checkForCode = $2;
      $endsWith = $3;
      if(exists($mgedmap{$checkForCode})) {
        $begining = $startsWith.$mgedmap{$checkForCode}.$endsWith;
      }
    }
##    print $out $begining."\n";
    if(exists($mgedmap{$name})) {
      print "$begining$rdf=\"";
      if( $isReference || $rdf eq "rdf:resource" ) {
        print "#";
      }
      print "$mgedmap{$name}\"";
      #check for slash
      if($end =~ /\s*\/>\n/) {
        print "/";
      }
      print ">\n";
    }
    else {
      print $_;
    }
  }
  elsif( $_ =~ /(.*<\/?)([A-Za-z_]*)(\s.*\n|>\n)/ ) {
    $startsWith = $1;
    $checkForCode = $2;
    $endsWith = $3;
    if(exists($mgedmap{$checkForCode})) {
      $checkForCode = $mgedmap{$checkForCode};
    }
    print $startsWith.$checkForCode.$endsWith;
  }
  elsif( $_ =~ /(.*>)(.*)(<\/rdf:first>\n)/ ) {
    $startsWith = $1;
    $checkForCode = $2;
    $endsWith = $3;
    if(exists($mgedmap{$checkForCode})) {
      $checkForCode = $mgedmap{$checkForCode};
    }
    print $startsWith.$checkForCode.$endsWith;
  }
  else {
    print $_;
  }
}

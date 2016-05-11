## Rob Wynne, LMCO
##
## MGED Conversion, First pass
## 1. Create Definition property declaration (header.owl)
## 2. Create PreferredName property declaration (header.owl)
## 3. Change rdfs:comment tags to Definition tags (global find/replace)
## 4. Create new property line for PrefrredName and populate with rdf:id or rdf"about"
## 5. For each NON-DeprecatedTerms instance, create a sublclass statement below and change the declaration to owl:Class
## 6. For DeprecatedTerms instances, create a subclass statement and change the declaration to owl:DeprecatedClass
## 7. For each instance, look to see if it has an rdf:type. If so, convert this to a subclass.
## 8. While processing, write the rdf:ID or rdf:about or rdf:resource and unique_identifier of each class to a mapping file
## 9. While processing, write the rdf:ID and unique identifier of instances to a mapping file.

open( $mgedinput, $ARGV[0] ) or die "Couldn't open file\n";
open $out, '>', "map.txt" or die "Couldn't create map file\n";
@lines = <$mgedinput>;
$linecount = @lines;
$classCount = 0;
$instanceCount = 0;
$i=0;

for( $i = 0; $i < $linecount; $i++ ) {
     $prefName = "";
     $uniqueId = "";
     $definition = "";

     ## owl:Class, owl:DatatypeProperty,
     ## owl:FunctionalProperty, owl:ObjectProperty
     ## owl:AnnotationProperty
     if( $lines[$i] =~ /(.*)owl:(Class|DatatypeProperty|FunctionalProperty|ObjectProperty|AnnotationProperty)\s(.*)=\"(.*)\">\n/ ) {
       $prefName = $4;
       $kind = $2;
       $rdfKind = $3;
       print "<owl:$kind $rdfKind=\"$prefName\">\n";
       if( $prefName =~ /#(.*)/ ) {
         $prefName = $1;
       }
       print "    <rdfs:label>$prefName</rdfs:label>\n";
       print "    <Preferred_Name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">$prefName</Preferred_Name>\n";
       $i++;
       while( $lines[$i] !~ /(.*)<\/owl:$kind>\n/ ) {
          if( $lines[$i] =~ /(.*)<rdfs:subClassOf>\n/ ) {
            while( $lines[$i] !~ /<\/rdfs:subClassOf>\n/ ) {
              if( $lines[$i] =~ /(.*)<owl:hasValue>\n/ ) {
                while( $lines[$i] !~ /(.*)<\/owl:hasValue>\n/ ) {
                  # Currently only expecting an instance.  I'm not sure if this would ever anticipate a class.
                  if( $lines[$i] =~ /(.*)<([A-Za-z]*)\s(.*)=\"(.*)\">/ ) {
                    $endtag = $2;
                    $internalPrefName = $4;
                    print "          <owl:Class rdf:ID=\"$internalPrefName\">\n";
                    if( $internalPrefName =~ /#(.*)/ ) {
                      $internalPrefName = $1;
                    }
                    print "          <rdfs:label>$internalPrefName</rdfs:label>\n";
		    print "          <Preferred_Name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">$internalPrefName</Preferred_Name>\n";
                    print "          <rdfs:subClassOf rdf:resource=\"$endtag\" \/>\n";
                    $i++;
                    while( $lines[$i] !~ /<\/$endtag>/ ) {
                      if( $lines[$i] =~ /(.*)<unique_identifier(.*)/ ) {
                        print $lines[$i];
                        $i++;
                        if( $lines[$i] =~ />(.*)<\/unique_identifier>\n/ ) {
                          print $lines[$i];
                          $internalUniqueId = $1;
                          print $out "$internalPrefName\t$internalUniqueId\n";
                          }
                      }
                      else {
                        print $lines[$i];
                      }
                      $i++;
                    }
                    $i++;
                    print "          </owl:Class>\n";
                  }
                  else {
                    print $lines[$i];
                    $i++;
                  }
                }
                print $lines[$i];
                $i++;
              }
              else {
                print $lines[$i];
                $i++;
              }
            }
            print "      <\/rdfs:subClassOf>\n";
          }
          elsif( $lines[$i] =~ /(.*)<unique_identifier(.*)/ ) {
            print $lines[$i];
            $i++;
            if( $lines[$i] =~ />(.*)<\/unique_identifier>\n/ ) {
                print $lines[$i];
                $uniqueId = $1;
            }
          }
          else {
            print $lines[$i];
          }
          $i++;
       }
       print "</owl:$kind>\n";
       $classCount++;
       print $out "$prefName\t$uniqueId\n";  #\towl:$kind
     }

     # DepcrecatedTerms
     elsif( $lines[$i] =~ /(.*)<DeprecatedTerms\s(.*)=\"(.*)\">\n/ ) {
       $tag = $2;
       $prefName = $3;
       print "  <owl:DeprecatedClass $tag=\"$prefName\">\n";
       if( $prefName =~ /#(.*)/ ) {
         $prefName = $1;
       }
       print "  <rdfs:label>$prefName</rdfs:label>\n";
       print "  <Preferred_Name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">$prefName</Preferred_Name>\n";
       print "  <rdfs:subClassOf rdf:resource=\"#DeprecatedTerms\"\/>\n";
       $i++;
       while( $lines[$i] !~ /<\/DeprecatedTerms>/ ) {
          if( $lines[$i] =~ /(.*)<rdfs:subClassOf>\n/ ) {
            while( $lines[$i] !~ /<\/rdfs:subClassOf>\n/ ) {
              ## ignore internal classes (for now)
              print $lines[$i];
              $i++
            }
            print "    <\/rdfs:subClassOf>\n";
          }
          elsif( $lines[$i] =~ /(.*)<(split_to_term|was_replaced_by|replaced_with_term|has_reason_for_deprecation)>\n/ ) {
            $internalUniqueId = "";
            $internalPrefName = "";
            $internalTag = $2;
            print $lines[$i];
            $i++;
            while( $lines[$i] !~ /<\/$internalTag>\n/ ) {
              if( $lines[$i] =~ /(.*)<([A-Z].*)\s(.*)=\"(.*)\">\n/ ) {
                $endtag = $2;
                $internalPrefName = $4;
                print "      <owl:Class rdf:ID=\"$internalPrefName\">\n";
                if( $internalPrefName =~ /#(.*)/ ) {
                  $internalPrefName = $1;
                }
                print "      <rdfs:label>$internalPrefName</rdfs:label>\n";
		print "      <Preferred_Name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">$internalPrefName</Preferred_Name>\n";
                print "      <rdfs:subClassOf rdf:resource=\"$endtag\" \/>\n";
                $i++;
                while( $lines[$i] !~ /<\/$endtag>/ ) {
                  if( $lines[$i] =~ /(.*)<unique_identifier(.*)/ ) {
                    print $lines[$i];
                    $i++;
                    if( $lines[$i] =~ />(.*)<\/unique_identifier>\n/ ) {
                           print $lines[$i];
                           $internalUniqueId = $1;
                           print $out "$internalPrefName\t$internalUniqueId\n";  #\tsplit_to_term
                    }
                  }
                  else {
                    print $lines[$i];
                  }
                  $i++;
                }
                $i++;
                print "      </owl:Class>\n";
              }
              else {
                print $lines[$i];
                $i++;
              }
            }
            print "    <\/$internalTag>\n";
          }
          elsif( $lines[$i] =~ /(.*)<unique_identifier(.*)/ ) {
            print $lines[$i];
            $i++;
            if( $lines[$i] =~ />(.*)<\/unique_identifier>\n/ ) {
                print $lines[$i];
                $uniqueId = $1;
            }
          }
          else {
            print $lines[$i];
          }
          $i++;
       }
       print "</owl:DeprecatedClass>\n";
       print $out "$prefName\t$uniqueId\n";  #\tDeprecated
       $classCount++;
     }

     #Instances
     elsif( $lines[$i] =~ /(.*)<([A-Za-z]*)\s(.*)=\"(.*)\">/ ) {
       $prefName = $4;
       $rdfID = $3;
       $subclass = $2;
       print "  <owl:Class $rdfID=\"$prefName\">\n";
       if( $prefName =~ /#(.*)/ ) {
         $prefName = $1;
       }
       print "  <rdfs:label>$prefName</rdfs:label>\n";
       print "  <Preferred_Name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">$prefName</Preferred_Name>\n";
       print "  <rdfs:subClassOf rdf:resource=\"#$subclass\"\/>\n";
       $i++;
       while( $lines[$i] !~ /<\/$subclass>/ ) {
         if( $lines[$i] =~ /(.*)<unique_identifier(.*)/ ) {
           print $lines[$i];
           $i++;
           if( $lines[$i] =~ />(.*)<\/unique_identifier>\n/ ) {
             print $lines[$i];
             $uniqueId = $1;
           }
         }
         elsif( $lines[$i] =~ /(.*)<rdf:type(.*)\/>\n/ ) {
           print "$1<rdfs:subClassOf$2/>\n";
         }
         else {
           print $lines[$i];
         }
         $i++;
       }
       print "  </owl:Class>\n";
       print $out "$prefName\t$uniqueId\n";  #\tInstance
       $instanceCount++;
     }

}
print "</rdf:RDF>\n";

close $out;

## print "There are $classCount classes\n";
## print "There are $instanceCount instances\n";

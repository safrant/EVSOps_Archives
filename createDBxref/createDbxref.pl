## Rob Wynne, LMCO
## Using Protege exported owl as input, create dbxref annotations for existing properties
##
## Config file is of the form:
## <old property> <tab> <new property>
##
## Example:
## BioCarta_ID <tab> biocarta_id
##
## annotation property...
##
## <BioCarta_ID rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
## >h_cxcr4Pathway</dbXref>
##
## becomes...
##
## <dbXref rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
## >biocarta_id:h_cxcr4Pathway</dbXref>
##
## Questions:
## 1) Create the new datatype property?
## 2) Remove old datatype properties? (may have annotations we want?)

use strict 'vars';

# Change these vars for the new
# property name and datatype
my $newProperty = "dbXref";
my $newPropertyDatatype = "rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\"";

open( my $owlfile, $ARGV[0] ) or die "Couldn't open OWL file!\n";
open( my $config, "config.txt" ) or die "Couldn't open config file!\n";
open my $out, '>', $ARGV[0]."-with-$newProperty.owl" or die "Couldn't create output file!\n";

my @owlLines = <$owlfile>;
my $owlLineCount = @owlLines;
my %xrefHoA;
my $i = 0;

while(<$config>) {
  next unless s/^(.*?)\t\s*//;
  $xrefHoA{$1} = [ split ];
}

for( $i = 0; $i < $owlLineCount; $i++ ) {
  if( $owlLines[$i] =~ /(.*)<([_a-zA-Z0-9-]*) rdf:datatype=.*\n/ ) {
    my $space = $1;
    my $this_property = $2;
    foreach my $property ( keys %xrefHoA ) {
      if( $property eq $this_property ) {
        if( $owlLines[$i+1] =~ /(.*)>(.*)<\/.*\n/ ) {
          $space = $1;
          my $val = $2;

          # Replace old property with the new
          $owlLines[$i] = $space."<$newProperty $newPropertyDatatype\n";
          $owlLines[++$i] = $space.">".$xrefHoA{$property}[0].":$val<\/$newProperty>\n";

        }
        break;
      }
    }
  }
}

print "Printing output...\n";
foreach(@owlLines) {
  print $out $_;
}
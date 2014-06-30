## create grammatical words
cat dictionary.dat | grep "GRAMMATIC" | sed -e 's/=.*//' | sed -e 's/\\ /\
/g' > grammatical.words

## create brandname words
cat dictionary.dat | grep "BRANDNAME" | sed -e 's/=.*//' | sed -e 's/\\ /\
/g' > brandname.words

## create en common words
cat dictionary.dat | grep "ENCOMMONWORD" | sed -e 's/=.*//' | sed -e 's/\\ /\
/g' > encommon.words
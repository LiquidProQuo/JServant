var:foo=foooo
var:bar=barrr
run:Notepad.exe
wait:2000
enter:This is variable foo: $(foo).
enter:This is variable bar: $(bar).
type:Next use a variable that does not exist causing the script to fail.
enter:This variable does not exist: $(DNE).
enter:Self Destruct Sequence. This will never be reached!

// NSA Codebreaker Challenge Medallion shellcode
char code[] = "\xeb\x32\x5b\x48\x89\xda\xb9\x1a\x00\x00\x00\x80\x33\xaa\x48\xff\xc3\x48\xff\xc9\x75\xf5\xb8\x01\x00\x00\x00\xbf\x01\x00\x00\x00\x48\x89\xd6\xba\x1a\x00\x00\x00\x0f\x05\xb8\x3c\x00\x00\x00\x48\x31\xff\x0f\x05\xe8\xc9\xff\xff\xff\xe4\xf9\xeb\x8a\xe9\xc5\xce\xcf\xc8\xd8\xcf\xcb\xc1\xcf\xd8\x8a\xe9\xc2\xcb\xc6\xc6\xcf\xc4\xcd\xcf\xa0";

int main(int argc, char **argv)
{
  int (*func)();
  func = (int (*)()) code;
  (int)(*func)();
}

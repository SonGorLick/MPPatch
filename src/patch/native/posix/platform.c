/**
    Copyright (C) 2015-2017 Lymia Aluysia <lymiahugs@gmail.com>

    Permission is hereby granted, free of charge, to any person obtaining a copy of
    this software and associated documentation files (the "Software"), to deal in
    the Software without restriction, including without limitation the rights to
    use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
    of the Software, and to permit persons to whom the Software is furnished to do
    so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
*/

#include <stdlib.h>
#include <stdbool.h>

#include <sys/mman.h>
#include <unistd.h>

#include "c_rt.h"
#include "c_defines.h"
#include "platform.h"

void setupProxyFunction(void* entry, const char* symbol) {
    void* ptr = resolveSymbol(symbol);
    if(!symbol) fatalError("Linking proxy defines symbol %s, which doesn't exist.", symbol);

    char buffer[1024];
    snprintf(buffer, sizeof(buffer), "proxy for %s", symbol);
    patchJmpInstruction(entry, ptr, buffer);
}

// Memory management functions
static int protectRange(size_t start, size_t length, int flags) {
    int page_size = getpagesize();
    size_t end = start + length;
    start = (start / page_size) * page_size;
    end   = ((end + page_size) / page_size) * page_size;
    return mprotect((void*) start, end - start, flags);
}
void unprotectMemoryRegion(void* start, size_t length, memory_oldProtect* old) {
  protectRange((size_t) start, length, PROT_READ | PROT_WRITE);
}
void protectMemoryRegion  (void* start, size_t length, memory_oldProtect* old) {
  protectRange((size_t) start, length, PROT_READ | PROT_EXEC);
}

ExecutableMemory* executable_malloc(int length) {
    ExecutableMemory* memory = (ExecutableMemory*) mmap(NULL, sizeof(ExecutableMemory) + length,
                                                        PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    memory->length = length;
    return memory;
}
void executable_prepare(ExecutableMemory* memory) {
    protectMemoryRegion(memory, memory->length, NULL);
}
void executable_free(ExecutableMemory* memory) {
    munmap(memory, memory->length);
}

// std::list implementation
#define CppList_length(list) ((__attribute__((may_alias)) int*) list->data)[0]
CppList* CppList_alloc() {
    CppList* list = CppListLink_alloc(sizeof(int));
    CppList_length(list) = 0;
    return list;
}
void* CppList_newLink(CppList* list, int length) {
    CppList_length(list)++;
    return CppListLink_newLink(list, length);
}
CppListLink* CppList_begin(CppList* list) {
    return list->next;
}
CppListLink* CppList_end(CppList* list) {
    return list;
}
int CppList_size(CppList* list) {
    return CppList_length(list);
}
void CppList_clear(CppList* list) {
    CppListLink_clear(list);
    CppList_length(list) = 0;
}
void CppList_free(CppList* list) {
    CppListLink_free(list);
}

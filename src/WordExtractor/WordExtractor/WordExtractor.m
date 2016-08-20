//
//  WordExtractor.m
//  WordExtractor
//
//  Created by TJT on 8/19/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import "WordExtractor.h"
#import "TextIterators.h"
#import "PhraseSearchTree.h"
#import "SubString.h"
#import "StringWrapper.h"

@interface Metadata : NSObject

@property (nonatomic, strong) NSString          *word;
@property (nonatomic, strong) NSString          *alternative;

@end

@implementation Metadata

@end

@implementation Result

- (instancetype)initWithWord:(NSString *)word index:(NSInteger)index {
    _word = word;
    _index = index;
    return self;
}

- (BOOL)isEqual:(id)other {
    if (other == self) {
        return YES;
    } else {
        if ([other isKindOfClass:[self class]]) {
            Result *r = other;
            return [r.word isEqual:_word];
        } else {
            return NO;
        }
    }
}

- (NSUInteger)hash {
    return _word.hash;
}

- (NSString *)description {
    return [_word stringByAppendingFormat:@"@%zd", _index];
}

@end

@interface WordExtractor ()

@property (nonatomic, strong) NSArray           *library;
@property (nonatomic, strong) NSMapTable        *map;
@property (nonatomic, strong) PhraseSearchTree  *searchTree;

@end

@implementation WordExtractor

- (instancetype)initWithLibrary:(NSArray *)library {
    _library = library;
    return self;
}

- (void)buildIndex {
    _map = [[NSMapTable alloc] initWithKeyOptions:NSPointerFunctionsStrongMemory valueOptions:NSPointerFunctionsStrongMemory capacity:20000];
    for (NSString *word in _library) {
        StringWrapper *wrapper = [StringWrapper stringWithString:word];
        Metadata *metadata = [_map objectForKey:wrapper];
        if (nil != metadata) {
            metadata.alternative = wrapper;
            continue;
        } else {
            metadata = [Metadata new];
        }
        metadata.word = word;
        [_map setObject:metadata forKey:wrapper];
    }
    
    _searchTree = [[PhraseSearchTree alloc] initWithLibrary:_library];
    [_searchTree build];
}

- (NSArray<Result *> *)extract:(NSString *)inputText {
    NSMutableSet<Result *> *results = [NSMutableSet new];
    SubString *subString = [SubString new];
    WordIterator *wordIterator = [WordIterator new];
    [wordIterator update:inputText];
    while ([wordIterator nextWord:subString]) {
        Metadata *m = [_map objectForKey:subString];
        if (nil != m) {
            if (nil != m.alternative) {
                [results addObject:[[Result alloc] initWithWord:m.alternative index:subString.start]];
            }
            [results addObject:[[Result alloc] initWithWord:m.word index:subString.start]];
        }
    }
    
    SentenceIterator *sentenceIterator = [SentenceIterator new];
    WordGroupIterator *wordGroupIterator = [WordGroupIterator new];
    [sentenceIterator update:inputText];
    SubString *subString1 = [SubString new];
    while ([sentenceIterator nextSentence:subString]) {
        [wordGroupIterator update:subString];
        while ([wordGroupIterator nextWordGroup:subString1]) {
            Metadata *m = [_map objectForKey:subString1];
            if (nil != m) {
                [results addObject:[[Result alloc] initWithWord:m.word index:subString1.start]];
            }
        }
        
        NSArray<NSString *> *words = [_searchTree search:subString];
        Metadata *m = [_map objectForKey:subString];
        if (nil != m) {
            [results addObject:[[Result alloc] initWithWord:m.word index:subString.start]];
        }
        if (nil != words) {
            for (NSString *s in words) {
                [results addObject:[[Result alloc] initWithWord:s index:subString.start]];
            }
        }
    }
    
    NSMutableArray<Result *> *array = [[NSMutableArray alloc] initWithCapacity:results.count];
    for (Result *r in results) {
        [array addObject:r];
    }
    [array sortUsingComparator:^(Result *r, Result *l) {
        return r.index - l.index;
    }];
    return array;
}

@end

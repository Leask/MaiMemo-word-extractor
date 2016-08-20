                                                                     //
//  PhraseSearchTree.m
//  WordExtractor
//
//  Created by TJT on 8/19/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import "PhraseSearchTree.h"
#import "TextUtil.h"
#import "StringWrapper.h"
#import "SubString.h"
#import "TextIterators.h"

@interface BuilderNode : NSObject<NSCopying>

@property (nonatomic, assign) NSUInteger                    hashCache;
@property (nonatomic, strong) NSMutableArray<NSString *>    *words;
@property (nonatomic, strong) NSArray<NSString *>           *array;

@end

@implementation BuilderNode

- (id)copyWithZone:(NSZone *)zone {
    return self;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _words = [NSMutableArray new];
        _hashCache = 0;
    }
    return self;
}

- (NSUInteger)hash {
    if (_hashCache != 0 && nil != _array && _array.count > 0) {
        for (NSString *s in _array) {
            for (NSInteger i = 0; i < s.length; i++) {
                _hashCache = _hashCache * 31 + [s characterAtIndex:i];
            }
        }
    }
    return _hashCache;
}

- (BOOL)isEqual:(id)other {
    if (other == self) {
        return YES;
    } else {
        if ([other isKindOfClass:[self class]]) {
            BuilderNode *n = other;
            if (n.array.count != _array.count) {
                return NO;
            }
            for (NSInteger i = 0; i < _array.count; i++) {
                if (![n.array[i] isEqual:_array[i]]) {
                    return NO;
                }
            }
            return YES;
        }
        return NO;
    }
}

@end

@interface Node : NSObject

@property (nonatomic, strong) NSArray<NSString *>              *word;
@property (nonatomic, strong) NSMutableArray<Node *>           *any;
@property (nonatomic, strong) NSMutableDictionary<NSString *, Node *>   *children;

@end

@implementation Node

- (instancetype)init {
    self = [super init];
    return self;
}

@end

@interface PhraseSearchTree ()

@property (nonatomic, strong) NSRegularExpression       *ANY_PATTERN;
@property (nonatomic, strong) Node                      *root;
@property (nonatomic, strong) NSArray<NSString *>       *library;
@property (nonatomic, strong) RangeArray                *splitResult;
@property (nonatomic, strong) SubString                 *subString;
@property (nonatomic, strong) WordIterator              *wordIterator;

@end

@implementation PhraseSearchTree

StringWrapper *ANY;

- (instancetype)initWithLibrary:(NSArray<NSString *> *)library {
    ANY = [StringWrapper stringWithString:@""];
    _ANY_PATTERN = [NSRegularExpression regularExpressionWithPattern:@" *(\\boneself\\b|\\bsomebody's\\b|\\bsomebody\\b|\\bsomething\\b|\\bone's\\b|\\bdo sth\\.|\\bsb's\\b|\\bdo sth\\b|\\bsb\\.'s\\b|\\bsb\\.|\\bsb\\b|\\bsth\\.|\\bsth\\b|\\.\\.\\.) *" options:NSRegularExpressionCaseInsensitive error:nil];
    _splitResult = [RangeArray new];
    _library = library;
    _isBuilded = NO;
    _wordIterator = [WordIterator new];
    _subString = [SubString new];
    return self;
}

- (BOOL)isAny:(NSString *)word {
    return [_ANY_PATTERN numberOfMatchesInString:word options:0 range:NSMakeRange(0, word.length)] > 0;
}

- (void)addNodeToTree:(BuilderNode *)node {
    NSArray *splittedWords = node.array;
    NSInteger size = splittedWords.count;
    Node *parent = _root;
    Node *child;
    for (NSInteger i = 0; i < size; i++) {
        child = nil;
        if (nil != parent.children) {
            child = [parent.children objectForKey:splittedWords[i]];
        }
        if (nil == child) {
            child = [Node new];
            if (i == size - 1) {
                child.word = node.words;
            }
            // is any
            if (splittedWords[i] == ANY) {
                if (nil == parent.any) {
                    parent.any = [NSMutableArray new];
                }
                [parent.any addObject:child];
            } else {
                if (nil == parent.children) {
                    parent.children = [NSMutableDictionary new];
                }
                [parent.children setObject:child forKey:splittedWords[i]];
            }
        }
        parent = child;
    }
}

- (void)build {
    NSMutableDictionary *builderNodeSet = [NSMutableDictionary new];
    _root = [Node new];
    _root.children = [NSMutableDictionary new];
    
    for (NSString *voc in _library) {
        if (NSNotFound == [voc rangeOfString:@" "].location) {
            continue;
        }
        
        if ([self isAny:voc]) {
            [_splitResult clear];
            [TextUtil split:voc separator:@" " outValue:_splitResult];
            NSMutableArray *split = [NSMutableArray new];
            for (NSInteger i = 0; i < _splitResult.count; i++) {
                NSString *s = [voc substringWithRange:NSMakeRange([_splitResult startAtIndex:i], [_splitResult endAtIndex:i] - [_splitResult startAtIndex:i])];
                if ([self isAny:s]) {
                    if (i != _splitResult.count - 1) {
                        [split addObject:ANY];
                    }
                } else {
                    [split addObject:[StringWrapper stringWithString:s]];
                }
            }
            
            BuilderNode *node = [BuilderNode new];
            [node.words addObject:voc];
            node.array = split;
            
            BuilderNode *n = [builderNodeSet objectForKey:node];
            if (nil != n) {
                [n.words addObject:voc];
            } else {
                [builderNodeSet setObject:node forKey:node];
            }
        }
    }
    for (BuilderNode *n in builderNodeSet) {
        [self addNodeToTree:n];
    }
    
    if (nil != _root.any) {
        for (Node *n in _root.any) {
            if (nil != n.children) {
                [_root.children setValuesForKeysWithDictionary:n.children];
            }
        }
    }
    _root.any = nil;
    self.isBuilded = YES;
}

- (NSArray *)search:(NSString *)sentence split:(RangeArray *)split start:(NSInteger)start {
    NSInteger size = split.count;
    NSArray<NSString *> *word = nil;
    Node *node = _root;
    for (NSInteger i = start; i < size; i++) {
        [_subString updateWithString:sentence start:[split startAtIndex:i] end:[split endAtIndex:i]];
        
        if (nil != node.any) {
            Node *nn;
            for (Node *n in node.any) {
                if (nil != n.children) {
                    nn = [n.children objectForKey:_subString];
                }
                if (nil != nn) {
                    node = nn;
                    break;
                }
            }
        }
        
        if (nil != node.word) {
            word = node.word;
            break;
        }
        
        Node *n = nil;
        if (nil != node.children) {
            n = [node.children objectForKey:_subString];
        }
        if (nil != n) {
            node = n;
        }
    }
    return word;
}

- (NSArray *)search:(NSString *)sentence {
    [_wordIterator update:sentence];
    _wordIterator.isSplitHyphen = NO;
    [_wordIterator justSplit:_splitResult];
    NSInteger size = _splitResult.count;
    if (size < 2) {
        return nil;
    }
    
    NSMutableArray<NSString *> *word = nil;
    
    for (NSInteger i = 0; i < size - 2; i++) {
        NSArray<NSString *> *words = [self search:sentence split:_splitResult start:i];
        if (nil != words) {
            if (nil == word) {
                word = [NSMutableArray new];
            }
            [word addObjectsFromArray:words];
        }
    }
    return word;
}

@end

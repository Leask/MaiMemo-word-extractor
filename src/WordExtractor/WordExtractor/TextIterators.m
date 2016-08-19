//
//  TextIterators.m
//  WordExtractor
//
//  Created by TJT on 8/18/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import "TextIterators.h"

@interface WordIterator ()

#define TYPE_LETTER 1
#define TYPE_HYPHEN 2
#define TYPE_QUOTE 3
#define TYPE_BREAK 4

@property (nonatomic, strong) NSString          *text;
@property (nonatomic, assign) NSUInteger        currentPos;
@property (nonatomic, assign) NSUInteger        length;
@property (nonatomic, strong) RangeArray        *inPleaceSearchResult;
@property (nonatomic, assign) NSUInteger        inPlaceSearchIndex;
@property (nonatomic, assign) NSUInteger        inPlaceSearchStart;
@property (nonatomic, assign) NSUInteger        start;
@property (nonatomic, assign) NSUInteger        end;
@property (nonatomic, strong) SubString         *subString;

@end

@implementation WordIterator

- (BOOL)isLetter:(unichar)c {
    return (c > 47 && c < 58) || (c > 64 && c < 91) || (c > 96 && c < 123);
}

- (NSInteger)getCharType:(unichar)c {
    if ([self isLetter:c]) {
        return TYPE_LETTER;
    } else if (c == '-') {
        return TYPE_HYPHEN;
    } else if (c == '\'') {
        return TYPE_QUOTE;
    }
    return TYPE_BREAK;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _isSplitHyphen = YES;
        _inPleaceSearchResult = [RangeArray new];
        _subString = [SubString new];
    }
    return self;
}

- (void)update:(NSString *)text {
    _text = text;
    _length = text.length;
    _currentPos = 0;
    [_inPleaceSearchResult clear];
    _inPlaceSearchIndex = 0;
    _inPlaceSearchStart = 0;
}

- (BOOL)nextWord:(SubString *)outValue {
    if ([self nextWord]) {
        [outValue updateWithString:_text start:_start end:_end];
        return YES;
    }
    return NO;
}

- (BOOL)nextWord {
    if (_isSplitHyphen && _inPleaceSearchResult.count > 0) {
        _start = _inPlaceSearchStart + [_inPleaceSearchResult startAtIndex:_inPlaceSearchIndex];
        _end = _inPlaceSearchStart + [_inPleaceSearchResult endAtIndex:_inPlaceSearchIndex];
        _inPlaceSearchIndex++;
        if (_inPlaceSearchIndex >= _inPleaceSearchResult.count) {
            [_inPleaceSearchResult clear];
            _inPlaceSearchIndex = 0;
        }
        return true;
    }
    if (_currentPos >= _length) {
        return false;
    }
    
    NSInteger start = _currentPos;
    
    // beginning must be a letter
    for (; start < _length; start++) {
        if ([self getCharType:[_text characterAtIndex:start]] == TYPE_LETTER) {
            break;
        }
    }
    
    // beginning is arrived end
    if (start + 1 > _length) {
        _currentPos = _length;
        return false;
    }
    
    NSInteger end = start + 1;
    NSInteger charType;
    BOOL containHyphen = NO;
    // search end of word
    for (; end < _length; end++) {
        charType = [self getCharType:[_text characterAtIndex:end]];
        if (charType == TYPE_HYPHEN) {
            containHyphen = YES;
        }
        if (charType == TYPE_BREAK) {
            break;
        }
    }
    
    if (end > _length) {
        end = _length;
    }
    _currentPos = end;
    
    // split words connect with '-'
    while ([_text characterAtIndex:end - 1] == '-') {
        end--;
        containHyphen = NO;
    }
    
    _start = start;
    _end = end;
    
    if (end <= start) {
        return NO;
    }
    
    if (containHyphen && _isSplitHyphen) {
        // cancel re-search when last char is '-'
        if ([_text characterAtIndex:end - 1] != '-') {
            _inPlaceSearchStart = start;
            [_subString updateWithString:_text start:start end:end];
            [TextUtil split:_subString separator:@"-" outValue:_inPleaceSearchResult];
        }
    }
    return YES;
}

- (void)justSplit:(RangeArray *)outValue {
    [outValue clear];
    while ([self nextWord]) {
        [outValue addWithStart:_start end:_end];
    }
}

- (NSUInteger)countByEnumeratingWithState:(NSFastEnumerationState *)state objects:(__unsafe_unretained id  _Nonnull *)buffer count:(NSUInteger)len {
    return 0;
}
@end



@interface WordGroupIterator ()

#define MINIMUM_WORD_GROUP_SIZE 1

@property (nonatomic, strong) NSString      *text;
@property (nonatomic, assign) NSInteger     currentPos;
@property (nonatomic, assign) NSInteger     currentSize;
@property (nonatomic, strong) RangeArray    *splitResult;
@property (nonatomic, assign) NSInteger     size;

@end

@implementation WordGroupIterator

- (instancetype)init {
    self = [super init];
    if (self) {
        _splitResult = [RangeArray new];
    }
    return self;
}

- (void)update:(NSString *)text {
    _text = text;
    _currentPos = 0;
    _currentSize = MINIMUM_WORD_GROUP_SIZE;
    [TextUtil split:_text separator:@" " outValue:_splitResult];
    _size = _splitResult.count;
}

- (BOOL)nextWordGroup:(SubString *)outValue {
    if (_size < 2) {
        return NO;
    }
    
    if (_currentPos + _currentSize >= _size || _currentSize >= 6) {
        _currentSize = MINIMUM_WORD_GROUP_SIZE;
        _currentPos++;
    }
    
    if (_currentPos == _size - 1) {
        return NO;
    }
    
    NSInteger start = [_splitResult startAtIndex:_currentPos];
    NSInteger end = [_splitResult endAtIndex:_currentPos + _currentSize];
    
    // first char muest be a letter
    for (; start < end; start++) {
        if ([TextUtil isLtter:[_text characterAtIndex:start]]) {
            break;
        }
    }
    
    NSInteger i = 0;
    // last char must be a letter and can't be '-'
    while (end > start) {
        unichar c = [_text characterAtIndex:end - 1];
        if ([TextUtil isLtter:c]) {
            if (i == 0 && c == '-' ) {
                end--;
                continue;
            }
            break;
        }
        i++;
        end--;
    }
    
    [outValue updateWithString:_text start:start end:end];
    _currentSize++;
    return YES;
}

@end


@interface SentenceIterator ()

@property (nonatomic, assign) NSInteger currentPos;
@property (nonatomic, assign) NSInteger length;
@property (nonatomic, strong) NSString  *text;

@end

@implementation SentenceIterator

- (void)update:(NSString *)text {
    _text = text;
    _length = _text.length;
    _currentPos = 0;
}

- (BOOL)lookBackward:(NSString *)word position:(NSInteger)pos {
    NSUInteger wordLen = word.length;
    if (pos > word.length) {
        for (NSInteger i = 1; i <= wordLen; i++) {
            unichar c1 = [TextUtil simpleToLower:[_text characterAtIndex:pos - i]];
            unichar c2 = [word characterAtIndex:wordLen - i];
            if (c1 != c2) {
                return NO;
            }
        }
        return YES;
    }
    return NO;
}

- (BOOL)nextSentence:(SubString *)outValue {
    if (_currentPos >= _length - 1) {
        return NO;
    }
    
    NSInteger start = _currentPos;
    if (_currentPos > 0) {
        start++;
    }
    unichar c;
    for (_currentPos += 3; _currentPos < _length; _currentPos++) {
        c = [_text characterAtIndex:_currentPos];
        if (c == ',' || c == '\n' || c == ';') {
            break;
        }
        if (c == '.') {
            // prevent break sentence when occured 'sth.', 'sb.', '...'
            if (_currentPos < _length - 1 && [_text characterAtIndex:_currentPos + 1] == '.') {
                continue;
            }
            if ([self lookBackward:@"sth" position:_currentPos] || [self lookBackward:@"sb" position:_currentPos] || [self lookBackward:@".." position:_currentPos]) {
                continue;
            }
            break;
        }
    }
    [outValue updateWithString:_text start:start end:_currentPos];
    return YES;
}

@end

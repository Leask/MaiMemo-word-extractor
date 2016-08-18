//
//  TextIterators.h
//  WordExtractor
//
//  Created by TJT on 8/18/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SubString.h"
#import "TextUtil.h"

@interface WordIterator : NSObject

@property (nonatomic, assign) BOOL          isSplitHyphen;

- (void)update:(NSString *)text;

- (BOOL)nextWord:(SubString *)outValue;

- (void)justSplit:(RangeArray *)outValue;

@end

@interface WordGroupIterator : NSObject

- (void)update:(NSString *)text;

- (BOOL)nextWordGroup:(SubString *)outValue;

@end

@interface SentenceIterator : NSObject

- (void)update:(NSString *)text;

- (BOOL)nextSentence:(SubString *)outValue;

@end
